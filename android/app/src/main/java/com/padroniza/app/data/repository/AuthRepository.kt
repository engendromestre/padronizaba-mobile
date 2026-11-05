package com.padroniza.app.data.repository

import android.content.Context
import com.padroniza.app.data.remote.PadronizaApiService
import com.padroniza.app.data.remote.RetrofitClient
import com.padroniza.app.data.remote.TokenManager
import com.padroniza.app.data.remote.dto.LoginRequest
import com.padroniza.app.data.remote.dto.UserDto

class AuthRepository(private val context: Context) {

    private val apiService: PadronizaApiService = RetrofitClient.getApiService(context)
    private val tokenManager: TokenManager = TokenManager(context)

    suspend fun login(email: String, password: String): Result<UserDto> {
        return try {
            val response = apiService.login(LoginRequest(email.trim(), password))
            if (response.isSuccessful && response.body()?.success == true) {
                val body = response.body()!!
                val token = body.token ?: return Result.failure(Exception("Token não retornado pelo servidor"))
                val user = body.user ?: return Result.failure(Exception("Dados de usuário não retornados"))

                tokenManager.salvarToken(token)
                tokenManager.salvarDadosUsuario(user.id, user.nome, user.email, user.role, user.empresaId)

                Result.success(user)
            } else {
                val erroMsg = response.body()?.error ?: "E-mail ou senha inválidos"
                Result.failure(Exception(erroMsg))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    fun logout() {
        tokenManager.limparSessao()
    }

    fun estaAutenticado(): Boolean {
        return tokenManager.estaAutenticado()
    }

    fun obterNomeUsuario(): String {
        return tokenManager.obterNomeUsuario()
    }
}
