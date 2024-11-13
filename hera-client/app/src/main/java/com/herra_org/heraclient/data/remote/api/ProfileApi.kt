package com.herra_org.heraclient.data.remote.api

import com.herra_org.heraclient.data.remote.dto.profile.ProfileResponseDto
import retrofit2.http.GET

interface ProfileApi {
    @GET("accounts/profile/")
    suspend fun getProfile(): ProfileResponseDto
}