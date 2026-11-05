package com.padroniza.app.data.remote

import android.content.Context
import android.content.SharedPreferences
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey

class TokenManager(context: Context) {

    private val masterKey = MasterKey.Builder(context)
        .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
        .build()

    private val prefs: SharedPreferences = EncryptedSharedPreferences.create(
        context,
        "padroniza_auth_prefs",
        masterKey,
        EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
        EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
    )

    fun salvarToken(token: String) {
        prefs.edit().putString(KEY_TOKEN, token).apply()
    }

    fun obterToken(): String? {
        return prefs.getString(KEY_TOKEN, null)
    }

    fun salvarDadosUsuario(id: String, nome: String, email: String, role: String, empresaId: String?) {
        prefs.edit()
            .putString(KEY_USER_ID, id)
            .putString(KEY_USER_NOME, nome)
            .putString(KEY_USER_EMAIL, email)
            .putString(KEY_USER_ROLE, role)
            .putString(KEY_USER_EMPRESA_ID, empresaId)
            .apply()
    }

    fun obterNomeUsuario(): String {
        return prefs.getString(KEY_USER_NOME, "Consultor") ?: "Consultor"
    }

    fun limparSessao() {
        prefs.edit().clear().apply()
    }

    fun estaAutenticado(): Boolean {
        return !obterToken().isNullOrBlank()
    }

    companion object {
        private const val KEY_TOKEN = "jwt_token"
        private const val KEY_USER_ID = "user_id"
        private const val KEY_USER_NOME = "user_nome"
        private const val KEY_USER_EMAIL = "user_email"
        private const val KEY_USER_ROLE = "user_role"
        private const val KEY_USER_EMPRESA_ID = "user_empresa_id"
    }
}
