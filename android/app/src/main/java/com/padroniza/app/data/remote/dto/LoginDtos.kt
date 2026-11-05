package com.padroniza.app.data.remote.dto

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class LoginRequest(
    val email: String,
    val password: String
)

@JsonClass(generateAdapter = true)
data class UserDto(
    val id: String,
    val nome: String,
    val email: String,
    val role: String,
    val empresaId: String?
)

@JsonClass(generateAdapter = true)
data class LoginResponse(
    val success: Boolean,
    val token: String?,
    val user: UserDto?,
    val error: String?
)
