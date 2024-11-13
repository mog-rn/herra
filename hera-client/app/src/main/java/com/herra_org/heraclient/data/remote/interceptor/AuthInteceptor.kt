package com.herra_org.heraclient.data.remote.interceptor

import com.herra_org.heraclient.data.local.TokenManager
import com.herra_org.heraclient.data.remote.api.AuthApi
import com.herra_org.heraclient.domain.model.auth.RefreshTokenRequest
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import okhttp3.Authenticator
import okhttp3.Request
import okhttp3.Response
import okhttp3.Route
import javax.inject.Inject

class AuthInterceptor @Inject constructor(
    private val tokenManager: TokenManager,
    private val authApi: AuthApi
) : Authenticator {

    override fun authenticate(route: Route?, response: Response): Request? {
        return runBlocking {
            try {
                val accessToken = tokenManager.getAccessToken().first()
                val refreshToken = tokenManager.getRefreshToken().first()

                if (accessToken == null || refreshToken == null) {
//                    Timber.d("No tokens available")
                    return@runBlocking null
                }

                // Only proceed if the response is unauthorized
                if (response.code != 401) {
                    return@runBlocking null
                }

                // Try to refresh the token
                val tokenResponse = authApi.refreshToken(
                    RefreshTokenRequest(
                        accessToken = accessToken,
                        refreshToken = refreshToken
                    )
                )

                // Save new tokens
                tokenManager.saveTokens(
                    accessToken = tokenResponse.tokens.accessToken,
                    refreshToken = tokenResponse.tokens.refreshToken,
                    expiresIn = tokenResponse.tokens.expiresIn
                )

                // Create new request with new token
                response.request.newBuilder()
                    .header("Authorization", "Bearer ${tokenResponse.tokens.accessToken}")
                    .build()

            } catch (e: Exception) {
//                Log.e(e, "Token refresh failed")
                // Clear tokens on refresh failure
                tokenManager.clearTokens()
                null
            }
        }
    }
}