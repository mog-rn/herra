package com.herra_org.heraclient.data.remote.api

import com.herra_org.heraclient.data.remote.dto.auth.*
import com.herra_org.heraclient.domain.model.auth.RefreshTokenRequest
import retrofit2.http.Body
import retrofit2.http.POST

interface AuthApi {
    @POST("accounts/register/")
    suspend fun register(@Body request: RegisterRequestDto): AuthResponseDto

    @POST("accounts/login/")
    suspend fun login(@Body request: LoginRequestDto): AuthResponseDto

    @POST("accounts/refresh-token/")
    suspend fun refreshToken(@Body request: RefreshTokenRequest): RefreshTokenResponseDto
}