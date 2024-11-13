package com.herra_org.heraclient.domain.repository

import com.herra_org.heraclient.domain.model.cycles.Cycle
import com.herra_org.heraclient.domain.model.cycles.CycleData
import com.herra_org.heraclient.domain.model.cycles.CycleDetail
import com.herra_org.heraclient.domain.model.cycles.Symptom
import com.herra_org.heraclient.utils.Resource
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate

interface HomeRepository {
    suspend fun getCurrentCycle(): Flow<Resource<Cycle>>
    suspend fun getCycleDetails(cycleId: Int): Flow<Resource<CycleDetail>>
    suspend fun logSymptom(
        cycleId: Int,
        type: String,
        severity: Int,
        date: LocalDate,
        notes: String? = null
    ): Flow<Resource<Symptom>>
    suspend fun getCycles(): List<CycleData>
    suspend fun createCycle(
        lastPeriodDate: String,
        cycleLength: Int,
        notes: String
    ): CycleData
}