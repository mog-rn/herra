package com.herra_org.heraclient.data.remote.repository

import android.os.Build
import androidx.annotation.RequiresApi
import com.herra_org.heraclient.data.remote.api.CycleApi
import com.herra_org.heraclient.data.remote.dto.cycles.SymptomLogRequestDto
import com.herra_org.heraclient.domain.model.cycles.Cycle
import com.herra_org.heraclient.domain.model.cycles.CycleData
import com.herra_org.heraclient.domain.model.cycles.CycleDetail
import com.herra_org.heraclient.domain.model.cycles.Symptom
import com.herra_org.heraclient.domain.model.cycles.toDomain
import com.herra_org.heraclient.domain.repository.HomeRepository
import com.herra_org.heraclient.utils.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import retrofit2.HttpException
import timber.log.Timber
import java.io.IOException
import java.time.LocalDate
import javax.inject.Inject

class HomeRepositoryImpl @Inject constructor(
    private val cycleApi: CycleApi
) : HomeRepository {

    companion object {
        private const val TAG = "HomeRepository"
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override suspend fun getCurrentCycle(): Flow<Resource<Cycle>> = flow {
        try {
            emit(Resource.Loading())
            val response = cycleApi.getCurrentCycle()
//            emit(Resource.Success(response.toDomain()))
        } catch (e: HttpException) {
            Timber.tag(TAG).e(e, "HTTP error getting current cycle")
            emit(
                Resource.Error(
                    message = when (e.code()) {
                        404 -> "No active cycle found"
                        401 -> "Please login again"
                        else -> "Failed to load cycle data"
                    }
                )
            )
        } catch (e: IOException) {
            Timber.tag(TAG).e(e, "IO error getting current cycle")
            emit(Resource.Error("Couldn't reach server. Check your internet connection."))
        } catch (e: Exception) {
            Timber.tag(TAG).e(e, "Error getting current cycle")
            emit(Resource.Error(e.localizedMessage ?: "An unexpected error occurred"))
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override suspend fun getCycleDetails(cycleId: Int): Flow<Resource<CycleDetail>> = flow {
        try {
            emit(Resource.Loading())
            val response = cycleApi.getCycleDetails(cycleId)
            emit(Resource.Success(response.toDomain()))
        } catch (e: HttpException) {
            Timber.tag(TAG).e(e, "HTTP error getting cycle details")
            emit(
                Resource.Error(
                    message = when (e.code()) {
                        404 -> "Cycle not found"
                        401 -> "Please login again"
                        else -> "Failed to load cycle details"
                    }
                )
            )
        } catch (e: IOException) {
            Timber.tag(TAG).e(e, "IO error getting cycle details")
            emit(Resource.Error("Couldn't reach server. Check your internet connection."))
        } catch (e: Exception) {
            Timber.tag(TAG).e(e, "Error getting cycle details")
            emit(Resource.Error(e.localizedMessage ?: "An unexpected error occurred"))
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override suspend fun logSymptom(
        cycleId: Int,
        type: String,
        severity: Int,
        date: LocalDate,
        notes: String?
    ): Flow<Resource<Symptom>> = flow {
        try {
            emit(Resource.Loading())
            val response = cycleApi.logSymptom(
                SymptomLogRequestDto(
                    cycleId = cycleId,
                    type = type,
                    severity = severity,
                    date = date.toString(),
                    notes = notes
                )
            )
            emit(Resource.Success(response.toDomain()))
        } catch (e: HttpException) {
            Timber.tag(TAG).e(e, "HTTP error logging symptom")
            emit(
                Resource.Error(
                    message = when (e.code()) {
                        404 -> "Cycle not found"
                        401 -> "Please login again"
                        400 -> "Invalid symptom data"
                        else -> "Failed to log symptom"
                    }
                )
            )
        } catch (e: IOException) {
            Timber.tag(TAG).e(e, "IO error logging symptom")
            emit(Resource.Error("Couldn't reach server. Check your internet connection."))
        } catch (e: Exception) {
            Timber.tag(TAG).e(e, "Error logging symptom")
            emit(Resource.Error(e.localizedMessage ?: "An unexpected error occurred"))
        }
    }

    override suspend fun getCycles(): List<CycleData> {
        TODO("Not yet implemented")
    }

    override suspend fun createCycle(
        lastPeriodDate: String,
        cycleLength: Int,
        notes: String
    ): CycleData {
        TODO("Not yet implemented")
    }

//    @RequiresApi(Build.VERSION_CODES.O)
//    override suspend fun getCurrentCycle(): Flow<Resource<Cycle>> = flow {
//        try {
//            emit(Resource.Loading())
//            val response = cycleApi.getCurrentCycle()
//            emit(Resource.Success(response.toDomain()))
//        } catch (e: HttpException) {
//            Timber.tag(TAG).e(e, "HTTP error getting current cycle")
//            emit(
//                Resource.Error(
//                    message = when (e.code()) {
//                        404 -> "No active cycle found"
//                        401 -> "Please login again"
//                        else -> "Failed to load cycle data"
//                    }
//                )
//            )
//        } catch (e: IOException) {
//            Timber.tag(TAG).e(e, "IO error getting current cycle")
//            emit(Resource.Error("Couldn't reach server. Check your internet connection."))
//        } catch (e: Exception) {
//            Timber.tag(TAG).e(e, "Error getting current cycle")
//            emit(Resource.Error(e.localizedMessage ?: "An unexpected error occurred"))
//        }
//    }
}