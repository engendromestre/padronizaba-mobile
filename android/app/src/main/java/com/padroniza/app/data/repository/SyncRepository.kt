package com.padroniza.app.data.repository

import android.content.Context
import com.padroniza.app.data.local.AppDatabase
import com.padroniza.app.data.local.dao.DadosApoioDao
import com.padroniza.app.data.local.entities.CategoriaEntity
import com.padroniza.app.data.local.entities.ClienteEntity
import com.padroniza.app.data.local.entities.FazendaEntity
import com.padroniza.app.data.local.entities.FrigorificoEntity
import com.padroniza.app.data.local.entities.GrupoWhatsappEntity
import com.padroniza.app.data.remote.PadronizaApiService
import com.padroniza.app.data.remote.RetrofitClient
import kotlinx.coroutines.flow.Flow

class SyncRepository(context: Context) {

    private val apiService: PadronizaApiService = RetrofitClient.getApiService(context)
    private val dadosApoioDao: DadosApoioDao = AppDatabase.getInstance(context).dadosApoioDao()

    suspend fun sincronizarCatalogo(): Result<Unit> {
        return try {
            val response = apiService.getDadosApoio()
            if (response.isSuccessful && response.body() != null) {
                val data = response.body()!!

                val frigorificos = data.frigorificos.map {
                    FrigorificoEntity(
                        id = it.id,
                        nome = it.nome,
                        cnpj = it.cnpj,
                        cidadeNome = it.cidade?.nome ?: "",
                        estadoUf = it.cidade?.estado?.uf ?: ""
                    )
                }

                val categorias = data.categorias.map {
                    CategoriaEntity(id = it.id, nome = it.nome)
                }

                val clientes = data.clientes.map {
                    ClienteEntity(
                        id = it.id,
                        nomeProdutor = it.nomeProdutor,
                        documento = it.documento,
                        empresaId = it.empresaId
                    )
                }

                val fazendas = data.clientes.flatMap { cli ->
                    cli.fazendas.map { faz ->
                        FazendaEntity(
                            id = faz.id,
                            clienteId = cli.id,
                            nome = faz.nome,
                            cidadeNome = faz.cidade?.nome ?: "",
                            estadoUf = faz.cidade?.estado?.uf ?: ""
                        )
                    }
                }

                val grupos = data.clientes.flatMap { cli ->
                    cli.grupos.map { grp ->
                        GrupoWhatsappEntity(
                            id = grp.id,
                            clienteId = cli.id,
                            nomeGrupo = grp.nomeGrupo,
                            groupIdWhatsapp = grp.groupIdWhatsapp
                        )
                    }
                }

                dadosApoioDao.atualizarCatalogoCompleto(
                    frigorificos = frigorificos,
                    categorias = categorias,
                    clientes = clientes,
                    fazendas = fazendas,
                    grupos = grupos
                )

                Result.success(Unit)
            } else {
                Result.failure(Exception("Falha ao baixar catálogo de apoio: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    fun listarFrigorificos(): Flow<List<FrigorificoEntity>> = dadosApoioDao.listarFrigorificos()
    fun listarCategorias(): Flow<List<CategoriaEntity>> = dadosApoioDao.listarCategorias()
    fun listarClientes(): Flow<List<ClienteEntity>> = dadosApoioDao.listarClientes()
    fun listarFazendasDoCliente(clienteId: String): Flow<List<FazendaEntity>> = dadosApoioDao.listarFazendasDoCliente(clienteId)
    fun listarGruposDoCliente(clienteId: String): Flow<List<GrupoWhatsappEntity>> = dadosApoioDao.listarGruposDoCliente(clienteId)
}
