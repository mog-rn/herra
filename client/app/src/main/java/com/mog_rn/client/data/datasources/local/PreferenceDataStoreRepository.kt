package com.mog_rn.client.data.datasources.local

import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.core.IOException
import com.mog_rn.client.data.repositories.UserPreferences
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import java.util.prefs.Preferences
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.stringPreferencesKey


const val TAG = "PREFERENCE_DATA_STORE_REPOSITORY"

class PreferenceDataStoreRepository @Inject constructor(
    private val dataStore: DataStore<Preferences>
) : UserPreferences {

    private companion object {
        val REFRESH_TOKEN = stringPreferencesKey("refresh_token")
        val ACCESS_TOKEN = stringPreferencesKey("access_token")
    }

    override suspend fun setAccessToken(accessToken: String) {
        dataStore.edit { tokens ->
            tokens[ACCESS_TOKEN] = accessToken
        }
    }

    override suspend fun setRefreshToken(refreshToken: String) {
        dataStore.edit { tokens ->
            tokens[REFRESH_TOKEN] = refreshToken
        }
    }

    override suspend fun removeAccessToken() {
        dataStore.edit { store ->
            if (store.contains(ACCESS_TOKEN)) {
                store.remove(ACCESS_TOKEN)
            }
        }
    }

    override suspend fun removeRefreshToken() {
        dataStore.edit { store ->
            if (store.contains(REFRESH_TOKEN)) {
                store.remove(REFRESH_TOKEN)
            }
        }
    }

    override suspend fun getAccessToken(): Result<String> {
        return Result.runCatching {
            val flow = dataStore.data
                .catch {
                    if (it is IOException) {
                        emit(emptyPreferences())
                        Log.e(TAG, "Error reading tokens.", it)
                    } else {
                        throw it
                    }
                }.map { preferences ->
                    preferences[ACCESS_TOKEN]
                }

            val value = flow.firstOrNull() ?: ""
            value
        }
    }

    override suspend fun getRefreshToken(): Result<String> {
        return Result.runCatching {
            val flow = dataStore.data
                .catch {
                    if (it is IOException) {
                        emit(emptyPreferences())
                        Log.e(TAG, "Error reading tokens.", it)
                    } else {
                        throw it
                    }
                }.map { preferences ->
                    preferences[REFRESH_TOKEN]
                }

            val value = flow.firstOrNull() ?: ""
            value
        }
    }

    val isLoggedInFlow: Flow<Boolean> = dataStore.data
        .catch {
            if (it is IOException) {
                emit(emptyPreferences())
                Log.e(TAG, "Error reading tokens.", it)
            } else {
                throw it
            }
        }.map { tokens ->
            val accessToken = tokens[ACCESS_TOKEN]
            try {
                val jwt = accessToken?.let { JWT(it) }
                if (jwt != null) {
                    val expiryTimeClaim = jwt.getClaim("exp")
                    val expiryTime = expiryTimeClaim.asLong()
                        ?.let { it1 -> java.util.concurrent.TimeUnit.SECONDS.toMillis(it1) }

                    val currentTime = System.currentTimeMillis()

                    if (expiryTime != null) {
                        expiryTime > currentTime
                    } else {
                        false
                    }
                } else {
                    false
                }
            } catch (e: DecodeException) {
                false
            }
        }
}