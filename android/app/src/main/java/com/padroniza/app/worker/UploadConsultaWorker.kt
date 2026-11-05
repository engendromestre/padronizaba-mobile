package com.padroniza.app.worker

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.padroniza.app.data.local.AppDatabase
import com.padroniza.app.data.local.entities.ConsultaEntity
import com.padroniza.app.data.local.entities.StatusEnvio
import com.padroniza.app.data.remote.RetrofitClient
import com.padroniza.app.data.remote.dto.ConsultaRequestDto

class UploadConsultaWorker(
    appContext: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(appContext, workerParams) {

    override suspend fun doWork(): Result {
        val database = AppDatabase.getInstance(applicationContext)
        val consultaDao = database.consultaDao()
        val apiService = RetrofitClient.getApiService(applicationContext)

        val pendentes = consultaDao.buscarConsultasPendentes()
        if (pendentes.isEmpty()) {
            return Result.success()
        }

        var teveFalhaDeRede = false

        for (consulta in pendentes) {
            try {
                val dto = mapearParaDto(consulta)
                val response = apiService.postConsulta(dto)

                if (response.isSuccessful) {
                    val body = response.body()
                    // 201 (criado novo) ou 200 (já processado anteriormente via idempotência)
                    if (body?.id != null || body?.status == "JA_PROCESSADA") {
                        consultaDao.marcarComoEnviado(consulta.localId)
                    }
                } else if (response.code() in 400..499) {
                    // Erro de validação de dados: marca como ERRO para o consultor revisar
                    val erroBody = response.errorBody()?.string() ?: "Erro de validação"
                    consultaDao.registrarFalhaEnvio(consulta.localId, erroBody)
                    consultaDao.atualizarStatus(consulta.localId, StatusEnvio.ERRO)
                } else {
                    // Erro 5xx de servidor: registra e tenta de novo mais tarde
                    teveFalhaDeRede = true
                    consultaDao.registrarFalhaEnvio(consulta.localId, "Servidor indisponível (${response.code()})")
                }
            } catch (e: Exception) {
                // Erro de rede / sem conexão: agenda retry com backoff exponencial
                teveFalhaDeRede = true
                consultaDao.registrarFalhaEnvio(consulta.localId, e.message ?: "Sem conexão com a internet")
            }
        }

        return if (teveFalhaDeRede) {
            Result.retry()
        } else {
            Result.success()
        }
    }

    private fun mapearParaDto(consulta: ConsultaEntity): ConsultaRequestDto {
        val grupos = consulta.gruposWhatsappIds
            .split(",")
            .map { it.trim() }
            .filter { it.isNotEmpty() }

        return ConsultaRequestDto(
            localId = consulta.localId,
            clienteId = consulta.clienteId,
            fazendaId = consulta.fazendaId,
            grupoWhatsappId = grupos,
            frigorificoId = consulta.frigorificoId,
            categoriaAnimalId = consulta.categoriaAnimalId,
            dataAbate = consulta.dataAbate,
            qtdAnimais = consulta.qtdAnimais,
            sexoLote = consulta.sexoLote,
            pesoVivoKg = consulta.pesoVivoKg,
            pesoTotalLoteKg = consulta.pesoTotalLoteKg,
            pesoCarcacaKg = consulta.pesoCarcacaKg,
            dentes0 = consulta.dentes0,
            dentes2 = consulta.dentes2,
            dentes4 = consulta.dentes4,
            dentes6 = consulta.dentes6,
            dentes8 = consulta.dentes8,
            gordura1 = consulta.gordura1,
            gordura2 = consulta.gordura2,
            gordura3 = consulta.gordura3,
            gordura4 = consulta.gordura4,
            gordura5 = consulta.gordura5,
            penalizacoes = consulta.penalizacoes,
            dif = consulta.dif,
            escoreRuminal = consulta.escoreRuminal,
            perdaVacinalKg = consulta.perdaVacinalKg,
            bezerrosP = consulta.bezerrosP,
            bezerrosM = consulta.bezerrosM,
            bezerrosG = consulta.bezerrosG,
            contaminacaoGiKg = consulta.contaminacaoGiKg,
            hematomasKg = consulta.hematomasKg,
            deslocamento = consulta.deslocamento,
            relatorioAbate = consulta.relatorioAbate
        )
    }
}
