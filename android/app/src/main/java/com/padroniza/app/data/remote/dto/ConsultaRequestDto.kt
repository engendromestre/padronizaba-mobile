package com.padroniza.app.data.remote.dto

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class ConsultaRequestDto(
    val localId: String,
    val clienteId: String,
    val clienteParceiroId: List<String> = emptyList(),
    val fazendaId: String,
    val grupoWhatsappId: List<String>,
    val frigorificoId: String,
    val categoriaAnimalId: String,
    val dataAbate: String,
    val qtdAnimais: Int,
    val sexoLote: String,
    val pesoVivoKg: Double,
    val pesoTotalLoteKg: Double,
    val pesoCarcacaKg: Double,

    // Maturidade
    val dentes0: Int,
    val dentes2: Int,
    val dentes4: Int,
    val dentes6: Int,
    val dentes8: Int,

    // Acabamento
    val gordura1: Int,
    val gordura2: Int,
    val gordura3: Int,
    val gordura4: Int,
    val gordura5: Int,

    // Adicionais
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
    val relatorioAbate: Boolean = false
)

@JsonClass(generateAdapter = true)
data class ConsultaResponseDto(
    val id: String?,
    val status: String?,
    val error: Any?
)
