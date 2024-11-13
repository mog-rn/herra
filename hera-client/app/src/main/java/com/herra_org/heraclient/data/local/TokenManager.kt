package com.herra_org.heraclient.data.local

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "auth_prefs")

@Singleton
class TokenManager @Inject constructor(
    private val context: Context
) {
    companion object {
        private val ACCESS_TOKEN = stringPreferencesKey("access_token")
        private val REFRESH_TOKEN = stringPreferencesKey("refresh_token")
        private val TOKEN_EXPIRY = longPreferencesKey("token_expiry")
    }

    suspend fun saveTokens(accessToken: String, refreshToken: String, expiresIn: Int) {
        context.dataStore.edit { preferences ->
            preferences[ACCESS_TOKEN] = accessToken
            preferences[REFRESH_TOKEN] = refreshToken
            preferences[TOKEN_EXPIRY] = System.currentTimeMillis() + (expiresIn * 1000)
        }
    }

    fun getAccessToken(): Flow<String?> = context.dataStore.data
        .map { preferences -> preferences[ACCESS_TOKEN] }

    fun getRefreshToken(): Flow<String?> = context.dataStore.data
        .map { preferences -> preferences[REFRESH_TOKEN] }

    fun getTokenExpiry(): Flow<Long?> = context.dataStore.data
        .map { preferences -> preferences[TOKEN_EXPIRY] }

    suspend fun clearTokens() {
        context.dataStore.edit { preferences ->
            preferences.clear()
        }
    }

    suspend fun isTokenValid(): Boolean {
        val expiry = context.dataStore.data.map { it[TOKEN_EXPIRY] ?: 0L }.first()
        return System.currentTimeMillis() < expiry
    }
}