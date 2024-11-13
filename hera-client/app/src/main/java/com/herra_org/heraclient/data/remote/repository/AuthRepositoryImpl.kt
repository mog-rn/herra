package com.herra_org.heraclient.data.remote.repository

import com.google.firebase.auth.FirebaseAuth
import com.herra_org.heraclient.data.local.TokenManager
import com.herra_org.heraclient.data.remote.api.AuthApi
import com.herra_org.heraclient.data.remote.dto.auth.*
import com.herra_org.heraclient.domain.model.auth.*
import com.herra_org.heraclient.domain.repository.AuthRepository
import com.herra_org.heraclient.utils.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await
import retrofit2.HttpException
import timber.log.Timber
import java.io.IOException

class AuthRepositoryImpl(
    private val api: AuthApi,
    private val firebaseAuth: FirebaseAuth,
    private val tokenManager: TokenManager
) : AuthRepository {

    companion object {
        private const val TAG = "AuthRepository"
    }

    override fun register(userRegistration: UserRegistration): Flow<Resource<AuthResponse>> = flow {
        try {
            emit(Resource.Loading())

            val response = api.register(
                RegisterRequestDto(
                    email = userRegistration.email,
                    password = userRegistration.password,
                    firstName = userRegistration.firstName,
                    lastName = userRegistration.lastName
                )
            )

            val authResponse = response.toDomain()

            response.customToken?.let { token ->
                try {
                    firebaseAuth.signInWithCustomToken(token).await()
                } catch (e: Exception) {
                    Timber.tag(TAG).e(e, "Firebase auth failed")
                    emit(Resource.Error("Firebase authentication failed"))
                    return@flow
                }
            }

            emit(Resource.Success(authResponse))

        } catch (e: HttpException) {
            Timber.tag(TAG).e(e, "Registration failed")
            emit(Resource.Error(
                message = when (e.code()) {
                    400 -> "Invalid registration details"
                    409 -> "Email already exists"
                    else -> e.localizedMessage ?: "An unexpected error occurred"
                }
            ))
        } catch (e: IOException) {
            Timber.tag(TAG).e(e, "Network error during registration")
            emit(Resource.Error("Couldn't reach server. Check your internet connection."))
        }
    }

    override fun login(userLogin: UserLogin): Flow<Resource<AuthResponse>> = flow {
        try {
            emit(Resource.Loading())

            val response = api.login(
                LoginRequestDto(
                    email = userLogin.email,
                    password = userLogin.password
                )
            )

            response.tokens?.let { tokens ->
                tokenManager.saveTokens(
                    accessToken = tokens.accessToken,
                    refreshToken = tokens.refreshToken,
                    expiresIn = tokens.expiresIn
                )
            }

            emit(Resource.Success(response.toDomain()))

        } catch (e: HttpException) {
            Timber.tag(TAG).e(e, "Login failed")
            emit(Resource.Error(
                message = when (e.code()) {
                    401 -> "Invalid email or password"
                    404 -> "Account not found"
                    else -> e.localizedMessage ?: "An unexpected error occurred"
                }
            ))
        } catch (e: IOException) {
            Timber.tag(TAG).e(e, "Network error during login")
            emit(Resource.Error("Couldn't reach server. Check your internet connection."))
        }
    }
}