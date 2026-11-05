package com.padroniza.app.data.remote

import com.padroniza.app.data.remote.dto.ConsultaRequestDto
import com.padroniza.app.data.remote.dto.ConsultaResponseDto
import com.padroniza.app.data.remote.dto.LoginRequest
import com.padroniza.app.data.remote.dto.LoginResponse
import com.padroniza.app.data.remote.dto.SyncResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface PadronizaApiService {

    @POST("api/mobile/auth/login")
    suspend fun login(@Body request: LoginRequest): Response<LoginResponse>

    @GET("api/mobile/sync/dados-apoio")
    suspend fun getDadosApoio(): Response<SyncResponse>

    @POST("api/consultas")
    suspend fun postConsulta(@Body request: ConsultaRequestDto): Response<ConsultaResponseDto>
}
