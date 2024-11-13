package com.herra_org.heraclient.data.remote.repository

import android.util.Log
import com.herra_org.heraclient.data.remote.api.ProfileApi
import com.herra_org.heraclient.domain.model.profile.Profile
import com.herra_org.heraclient.domain.model.profile.toDomain
import com.herra_org.heraclient.domain.repository.ProfileRepository
import com.herra_org.heraclient.utils.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import retrofit2.HttpException
import timber.log.Timber
import java.io.IOException
import javax.inject.Inject

class ProfileRepositoryImpl @Inject constructor(
    private val api: ProfileApi
) : ProfileRepository {

    override suspend fun getProfile(): Flow<Resource<Profile>> = flow {
        try {
            emit(Resource.Loading())
            val response = api.getProfile()
            emit(Resource.Success(response.data.toDomain()))
        } catch (e: HttpException) {
            Timber.tag(TAG).e(e, "HTTP error getting profile")
            emit(Resource.Error(
                message = when (e.code()) {
                    401 -> "Please login again"
                    else -> "Failed to load profile"
                }
            ))
        } catch (e: IOException) {
            Timber.tag(TAG).e(e, "IO error getting profile")
            emit(Resource.Error("Couldn't reach server. Check your internet connection."))
        } catch (e: Exception) {
            Timber.tag(TAG).e(e, "Error getting profile")
            emit(Resource.Error(e.localizedMessage ?: "An unexpected error occurred"))
        }
    }

    companion object {
        private const val TAG = "ProfileRepository"
    }
}