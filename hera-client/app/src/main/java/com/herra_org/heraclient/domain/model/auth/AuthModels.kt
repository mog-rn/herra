package com.herra_org.heraclient.domain.model.auth

data class UserRegistration(
    val email: String,
    val password: String,
    val firstName: String,
    val lastName: String
)

data class UserLogin(
    val email: String,
    val password: String
)

data class User(
    val id: Int,
    val email: String,
    val firebaseUid: String,
    val firstName: String,
    val lastName: String,
    val isActive: Boolean = true
)

data class TokenResponse(
    val accessToken: String,
    val refreshToken: String,
    val expiresIn: Int
)

data class AuthResponse(
    val message: String,
    val user: User,
    val tokens: TokenResponse? = null,
    val customToken: String? = null
)

data class RefreshTokenRequest(
    val accessToken: String,
    val refreshToken: String
)

data class RefreshTokenResponse(
    val message: String,
    val tokens: Tokens
)

data class Tokens(
    val accessToken: String,
    val refreshToken: String,
    val expiresIn: Int
)