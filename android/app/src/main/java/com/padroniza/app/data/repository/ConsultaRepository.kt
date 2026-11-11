package com.padroniza.app.data.repository

import android.content.Context
import com.padroniza.app.data.local.AppDatabase
import com.padroniza.app.data.local.dao.ConsultaDao
import com.padroniza.app.data.local.entities.ConsultaEntity
import com.padroniza.app.data.local.entities.StatusEnvio
import com.padroniza.app.worker.SyncManager
import kotlinx.coroutines.flow.Flow
import java.math.BigDecimal
import java.math.RoundingMode
import java.util.UUID

data class TotaisCarcaça(
    val mediaArroba: Double,
    val rcPercentual: Double,
    val dentes0Pct: Double,
    val dentes2Pct: Double,
    val dentes4Pct: Double,
    val dentes6Pct: Double,
    val dentes8Pct: Double,
    val gordura1Pct: Double,
    val gordura2Pct: Double,
    val gordura3Pct: Double,
    val gordura4Pct: Double,
    val gordura5Pct: Double
)

class ConsultaRepository(private val context: Context) {

    private val consultaDao: ConsultaDao = AppDatabase.getInstance(context).consultaDao()

    fun listarConsultas(): Flow<List<ConsultaEntity>> {
        return consultaDao.listarTodasConsultas()
    }

    suspend fun buscarPorId(localId: String): ConsultaEntity? {
        return consultaDao.buscarPorId(localId)
    }

    suspend fun salvarRascunho(consulta: ConsultaEntity) {
        val entidade = if (consulta.localId.isBlank()) {
            consulta.copy(localId = UUID.randomUUID().toString())
        } else {
            consulta
        }
        consultaDao.salvarConsulta(entidade.copy(statusEnvio = StatusEnvio.RASCUNHO))
    }

    suspend fun finalizarEEnviar(consulta: ConsultaEntity) {
        val entidadePronta = consulta.copy(
            localId = if (consulta.localId.isBlank()) UUID.randomUUID().toString() else consulta.localId,
            statusEnvio = StatusEnvio.PENDENTE_ENVIO
        )

        // 1. Grava no Room local SQLite (persistência garantida)
        consultaDao.salvarConsulta(entidadePronta)

        // 2. Dispara WorkManager para sincronizar em segundo plano com retry exponencial
        SyncManager.agendarUploadImediato(context)
    }

    suspend fun excluirConsulta(localId: String) {
        consultaDao.excluirConsulta(localId)
    }

    suspend fun reenviarConsulta(localId: String) {
        consultaDao.atualizarStatus(localId, StatusEnvio.PENDENTE_ENVIO)
        SyncManager.agendarUploadImediato(context)
    }

    suspend fun reenviarTodasComFalha() {
        consultaDao.marcarFalhasComoPendentes()
        SyncManager.agendarUploadImediato(context)
    }

    /**
     * Calcula métricas de arroba e rendimento de carcaça
     * idênticas ao backend (src/lib/schemas/consulta.ts)
     */
    fun calcularMetricas(
        qtdAnimais: Int,
        pesoTotalLoteKg: Double,
        pesoVivoKg: Double,
        d0: Int, d2: Int, d4: Int, d6: Int, d8: Int,
        g1: Int, g2: Int, g3: Int, g4: Int, g5: Int
    ): TotaisCarcaça {
        if (qtdAnimais <= 0) {
            return TotaisCarcaça(0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0)
        }

        val qtd = qtdAnimais.toDouble()

        // Média @: (pesoTotalLote / qtd) / 15
        val mediaArroba = if (pesoTotalLoteKg > 0) {
            arredondar((pesoTotalLoteKg / qtd) / 15.0)
        } else 0.0

        // RC%: (pesoTotalLote / (pesoVivo * qtd)) * 100
        val rc = if (pesoTotalLoteKg > 0 && pesoVivoKg > 0) {
            arredondar((pesoTotalLoteKg / (pesoVivoKg * qtd)) * 100.0)
        } else 0.0

        // Dentição %
        val d0Pct = arredondar((d0 / qtd) * 100.0)
        val d2Pct = arredondar((d2 / qtd) * 100.0)
        val d4Pct = arredondar((d4 / qtd) * 100.0)
        val d6Pct = arredondar((d6 / qtd) * 100.0)
        val d8Pct = arredondar((d8 / qtd) * 100.0)

        // Acabamento %
        val g1Pct = arredondar((g1 / qtd) * 100.0)
        val g2Pct = arredondar((g2 / qtd) * 100.0)
        val g3Pct = arredondar((g3 / qtd) * 100.0)
        val g4Pct = arredondar((g4 / qtd) * 100.0)
        val g5Pct = arredondar((g5 / qtd) * 100.0)

        return TotaisCarcaça(
            mediaArroba = mediaArroba,
            rcPercentual = rc,
            dentes0Pct = d0Pct,
            dentes2Pct = d2Pct,
            dentes4Pct = d4Pct,
            dentes6Pct = d6Pct,
            dentes8Pct = d8Pct,
            gordura1Pct = g1Pct,
            gordura2Pct = g2Pct,
            gordura3Pct = g3Pct,
            gordura4Pct = g4Pct,
            gordura5Pct = g5Pct
        )
    }

    private fun arredondar(valor: Double): Double {
        return BigDecimal(valor).setScale(2, RoundingMode.HALF_UP).toDouble()
    }
}
