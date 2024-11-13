package com.herra_org.heraclient.data.remote.dto.auth

import com.google.gson.annotations.SerializedName
import com.herra_org.heraclient.domain.model.auth.AuthResponse
import com.herra_org.heraclient.domain.model.auth.TokenResponse
import com.herra_org.heraclient.domain.model.auth.User

data class TokenResponseDto(
    @SerializedName("access_token") val accessToken: String,
    @SerializedName("refresh_token") val refreshToken: String,
    @SerializedName("expires_in") val expiresIn: Int
)

data class UserDto(
    @SerializedName("id") val id: Int,
    @SerializedName("email") val email: String,
    @SerializedName("firebase_uid") val firebaseUid: String,
    @SerializedName("first_name") val firstName: String,
    @SerializedName("last_name") val lastName: String,
    @SerializedName("is_active") val isActive: Boolean = true
)

data class LoginRequestDto(
    @SerializedName("email") val email: String,
    @SerializedName("password") val password: String
)

data class RegisterRequestDto(
    @SerializedName("email") val email: String,
    @SerializedName("password") val password: String,
    @SerializedName("first_name") val firstName: String,
    @SerializedName("last_name") val lastName: String
)


data class AuthResponseDto(
    @SerializedName("message") val message: String,
    @SerializedName("user") val user: UserDto,
    @SerializedName("tokens") val tokens: TokenResponseDto? = null,
    @SerializedName("custom_token") val customToken: String? = null
)

data class TokensDto(
    @SerializedName("access_token") val accessToken: String,
    @SerializedName("refresh_token") val refreshToken: String,
    @SerializedName("expires_in") val expiresIn: Int
)

data class RefreshTokenResponseDto(
    @SerializedName("message") val message: String,
    @SerializedName("tokens") val tokens: TokensDto
)

fun TokenResponseDto.toDomain() = TokenResponse(
    accessToken = accessToken,
    refreshToken = refreshToken,
    expiresIn = expiresIn
)

fun UserDto.toDomain() = User(
    id = id,
    email = email,
    firebaseUid = firebaseUid,
    firstName = firstName,
    lastName = lastName,
    isActive = isActive
)

fun AuthResponseDto.toDomain() = AuthResponse(
    message = message,
    user = user.toDomain(),
    tokens = tokens?.toDomain(),
    customToken = customToken
)