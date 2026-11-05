package com.padroniza.app.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "consultas")
data class ConsultaEntity(
    @PrimaryKey
    val localId: String, // UUID gerado no aparelho para garantir idempotência

    // Etapa 1 - Identificação
    val clienteId: String,
    val clienteNome: String,
    val fazendaId: String,
    val fazendaNome: String,
    val frigorificoId: String,
    val frigorificoNome: String,
    val categoriaAnimalId: String,
    val categoriaNome: String,
    val dataAbate: String, // YYYY-MM-DD
    val gruposWhatsappIds: String, // IDs separados por vírgula

    // Etapa 2 - Animais e Peso
    val qtdAnimais: Int,
    val sexoLote: String, // "MACHO" ou "FEMEA"
    val pesoVivoKg: Double,
    val pesoTotalLoteKg: Double,
    val pesoCarcacaKg: Double,
    val mediaArroba: Double,
    val rcPercentual: Double,

    // Etapa 3 - Maturidade (Dentição)
    val dentes0: Int = 0,
    val dentes2: Int = 0,
    val dentes4: Int = 0,
    val dentes6: Int = 0,
    val dentes8: Int = 0,
    val dentes0Pct: Double = 0.0,
    val dentes2Pct: Double = 0.0,
    val dentes4Pct: Double = 0.0,
    val dentes6Pct: Double = 0.0,
    val dentes8Pct: Double = 0.0,

    // Etapa 4 - Acabamento (Gordura)
    val gordura1: Int = 0,
    val gordura2: Int = 0,
    val gordura3: Int = 0,
    val gordura4: Int = 0,
    val gordura5: Int = 0,
    val gordura1Pct: Double = 0.0,
    val gordura2Pct: Double = 0.0,
    val gordura3Pct: Double = 0.0,
    val gordura4Pct: Double = 0.0,
    val gordura5Pct: Double = 0.0,

    // Etapa 5 - Adicionais
    val penalizacoes: String? = null,
    val dif: String? = null,
    val escoreRuminal: String? = null,
    val perdaVacinalKg: Double? = null,
    val bezerrosP: Int = 0,
    val bezerrosM: Int = 0,
    val bezerrosG: Int = 0,
    val contaminacaoGiKg: Double? = null,
    val hematomasKg: Double? = null,
    val deslocamento: Boolean = false,
    val relatorioAbate: Boolean = false,

    // Metadados de Sincronização Offline
    val statusEnvio: StatusEnvio = StatusEnvio.RASCUNHO,
    val criadoEm: Long = System.currentTimeMillis(),
    val enviadoEm: Long? = null,
    val tentativasEnvio: Int = 0,
    val ultimoErro: String? = null
)
