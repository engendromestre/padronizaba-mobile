package com.padroniza.app.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.padroniza.app.data.local.entities.CategoriaEntity
import com.padroniza.app.data.local.entities.ClienteEntity
import com.padroniza.app.data.local.entities.FazendaEntity
import com.padroniza.app.data.local.entities.FrigorificoEntity
import com.padroniza.app.data.local.entities.GrupoWhatsappEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface DadosApoioDao {

    // --- Frigoríficos ---
    @Query("SELECT * FROM frigorificos ORDER BY nome ASC")
    fun listarFrigorificos(): Flow<List<FrigorificoEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun inserirFrigorificos(frigorificos: List<FrigorificoEntity>)

    @Query("DELETE FROM frigorificos")
    suspend fun limparFrigorificos()

    // --- Categorias ---
    @Query("SELECT * FROM categorias ORDER BY nome ASC")
    fun listarCategorias(): Flow<List<CategoriaEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun inserirCategorias(categorias: List<CategoriaEntity>)

    @Query("DELETE FROM categorias")
    suspend fun limparCategorias()

    // --- Clientes ---
    @Query("SELECT * FROM clientes ORDER BY nomeProdutor ASC")
    fun listarClientes(): Flow<List<ClienteEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun inserirClientes(clientes: List<ClienteEntity>)

    @Query("DELETE FROM clientes")
    suspend fun limparClientes()

    // --- Fazendas ---
    @Query("SELECT * FROM fazendas WHERE clienteId = :clienteId ORDER BY nome ASC")
    fun listarFazendasDoCliente(clienteId: String): Flow<List<FazendaEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun inserirFazendas(fazendas: List<FazendaEntity>)

    @Query("DELETE FROM fazendas")
    suspend fun limparFazendas()

    // --- Grupos de WhatsApp ---
    @Query("SELECT * FROM grupos_whatsapp WHERE clienteId = :clienteId ORDER BY nomeGrupo ASC")
    fun listarGruposDoCliente(clienteId: String): Flow<List<GrupoWhatsappEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun inserirGrupos(grupos: List<GrupoWhatsappEntity>)

    @Query("DELETE FROM grupos_whatsapp")
    suspend fun limparGrupos()

    // --- Transação atômica de sincronização completa ---
    @Transaction
    suspend fun atualizarCatalogoCompleto(
        frigorificos: List<FrigorificoEntity>,
        categorias: List<CategoriaEntity>,
        clientes: List<ClienteEntity>,
        fazendas: List<FazendaEntity>,
        grupos: List<GrupoWhatsappEntity>
    ) {
        limparFrigorificos()
        inserirFrigorificos(frigorificos)

        limparCategorias()
        inserirCategorias(categorias)

        limparClientes()
        inserirClientes(clientes)

        limparFazendas()
        inserirFazendas(fazendas)

        limparGrupos()
        inserirGrupos(grupos)
    }
}
