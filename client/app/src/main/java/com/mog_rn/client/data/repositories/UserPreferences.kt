package com.mog_rn.client.data.repositories

interface UserPreferences {
    suspend fun setAccessToken(accessToken: String)
    suspend fun setRefreshToken(refreshToken: String)

    suspend fun removeAccessToken()
    suspend fun removeRefreshToken()

    suspend fun getAccessToken(): Result<String>
    suspend fun getRefreshToken(): Result<String>
}