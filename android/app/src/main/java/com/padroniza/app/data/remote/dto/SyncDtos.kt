package com.padroniza.app.data.remote.dto

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class EstadoDto(
    val id: String?,
    val uf: String,
    val nome: String?
)

@JsonClass(generateAdapter = true)
data class CidadeDto(
    val id: String?,
    val nome: String,
    val estado: EstadoDto?
)

@JsonClass(generateAdapter = true)
data class FrigorificoDto(
    val id: String,
    val nome: String,
    val cnpj: String?,
    val cidade: CidadeDto?
)

@JsonClass(generateAdapter = true)
data class CategoriaDto(
    val id: String,
    val nome: String
)

@JsonClass(generateAdapter = true)
data class FazendaDto(
    val id: String,
    val nome: String,
    val cidade: CidadeDto?
)

@JsonClass(generateAdapter = true)
data class GrupoWhatsappDto(
    val id: String,
    val nomeGrupo: String,
    val groupIdWhatsapp: String
)

@JsonClass(generateAdapter = true)
data class ClienteDto(
    val id: String,
    val nomeProdutor: String,
    val documento: String,
    val empresaId: String,
    val fazendas: List<FazendaDto> = emptyList(),
    val grupos: List<GrupoWhatsappDto> = emptyList()
)

@JsonClass(generateAdapter = true)
data class SyncResponse(
    val timestamp: String,
    val versao: String,
    val categorias: List<CategoriaDto> = emptyList(),
    val frigorificos: List<FrigorificoDto> = emptyList(),
    val clientes: List<ClienteDto> = emptyList()
)
