package com.padroniza.app.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "frigorificos")
data class FrigorificoEntity(
    @PrimaryKey val id: String,
    val nome: String,
    val cnpj: String?,
    val cidadeNome: String,
    val estadoUf: String
)

@Entity(tableName = "categorias")
data class CategoriaEntity(
    @PrimaryKey val id: String,
    val nome: String
)

@Entity(tableName = "clientes")
data class ClienteEntity(
    @PrimaryKey val id: String,
    val nomeProdutor: String,
    val documento: String,
    val empresaId: String
)

@Entity(tableName = "fazendas")
data class FazendaEntity(
    @PrimaryKey val id: String,
    val clienteId: String,
    val nome: String,
    val cidadeNome: String,
    val estadoUf: String
)

@Entity(tableName = "grupos_whatsapp")
data class GrupoWhatsappEntity(
    @PrimaryKey val id: String,
    val clienteId: String,
    val nomeGrupo: String,
    val groupIdWhatsapp: String
)
