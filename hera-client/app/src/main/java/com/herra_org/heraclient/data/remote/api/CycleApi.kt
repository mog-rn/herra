package com.herra_org.heraclient.data.remote.api

import com.herra_org.heraclient.data.remote.dto.cycles.CycleDetailResponseDto
import com.herra_org.heraclient.data.remote.dto.cycles.CycleResponseDto
import com.herra_org.heraclient.data.remote.dto.cycles.SymptomLogRequestDto
import com.herra_org.heraclient.data.remote.dto.cycles.SymptomLogResponseDto
import com.herra_org.heraclient.data.remote.requests.AddSymptomRequest
import com.herra_org.heraclient.domain.model.cycles.CreateCycleRequest
import com.herra_org.heraclient.domain.model.cycles.Cycle
import com.herra_org.heraclient.domain.model.cycles.CycleData
import com.herra_org.heraclient.domain.model.cycles.SymptomData
import retrofit2.Response
import retrofit2.http.*

interface CycleApi {
    @GET("cycle/cycles/")
    suspend fun getCycles(): Response<List<CycleData>>

    @GET("cycle/cycles/current/")
    suspend fun getCurrentCycle(): Cycle

    @POST("cycle/cycles/")
    suspend fun createCycle(@Body request: CreateCycleRequest): Response<CycleData>


    @GET("cycles/{cycleId}/")
    suspend fun getCycleDetails(@Path("cycleId") cycleId: Int): CycleDetailResponseDto

    @POST("cycles/symptoms/")
    suspend fun logSymptom(@Body request: SymptomLogRequestDto): SymptomLogResponseDto

    @POST("cycle/cycles/{cycleId}/add_symptom/")
    suspend fun addSymptom(
        @Path("cycleId") cycleId: Int,
        @Body request: AddSymptomRequest
    ): Response<SymptomData>
}