package com.padroniza.app.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.padroniza.app.data.local.entities.ConsultaEntity
import com.padroniza.app.data.local.entities.StatusEnvio
import kotlinx.coroutines.flow.Flow

@Dao
interface ConsultaDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun salvarConsulta(consulta: ConsultaEntity)

    @Update
    suspend fun atualizarConsulta(consulta: ConsultaEntity)

    @Query("SELECT * FROM consultas ORDER BY criadoEm DESC")
    fun listarTodasConsultas(): Flow<List<ConsultaEntity>>

    @Query("SELECT * FROM consultas WHERE localId = :localId LIMIT 1")
    suspend fun buscarPorId(localId: String): ConsultaEntity?

    @Query("SELECT * FROM consultas WHERE localId = :localId LIMIT 1")
    fun observarConsulta(localId: String): Flow<ConsultaEntity?>

    @Query("SELECT * FROM consultas WHERE statusEnvio = 'PENDENTE_ENVIO' ORDER BY criadoEm ASC")
    suspend fun buscarConsultasPendentes(): List<ConsultaEntity>

    @Query("UPDATE consultas SET statusEnvio = 'ENVIADO', enviadoEm = :enviadoEm WHERE localId = :localId")
    suspend fun marcarComoEnviado(localId: String, enviadoEm: Long = System.currentTimeMillis())

    @Query("UPDATE consultas SET tentativasEnvio = tentativasEnvio + 1, ultimoErro = :erro WHERE localId = :localId")
    suspend fun registrarFalhaEnvio(localId: String, erro: String)

    @Query("UPDATE consultas SET statusEnvio = :status WHERE localId = :localId")
    suspend fun atualizarStatus(localId: String, status: StatusEnvio)

    @Query("UPDATE consultas SET statusEnvio = 'PENDENTE_ENVIO' WHERE statusEnvio = 'ERRO'")
    suspend fun marcarFalhasComoPendentes()

    @Query("DELETE FROM consultas WHERE localId = :localId")
    suspend fun excluirConsulta(localId: String)
}
