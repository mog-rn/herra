// domain/model/CycleData.kt
package com.herra_org.heraclient.domain.model.cycles

data class CycleData(
    val id: Int,
    val startDate: String,
    val cycleLength: Int,
    val currentPhase: CyclePhase,
    val lastPeriodDate: String,
    val notes: String?,
    val isActive: Boolean,
    val createdAt: String,
    val updatedAt: String,
    val symptoms: List<SymptomData>? = null,
    val daysUntilNextPeriod: Int? = null
)


// domain/model/SymptomData.kt
data class SymptomData(
    val id: Int,
    val date: String,
    val mood: Int?,
    val energy: Int?,
    val flowIntensity: Int?,
    val painLevel: Int?,
    val notes: String?,
    val createdAt: String
)

// For API requests, create request models:
// data/remote/request/CreateCycleRequest.kt
data class CreateCycleRequest(
    val lastPeriodDate: String,
    val cycleLength: Int,
    val notes: String?
)

// Now update the HomeRepository interface:
interface HomeRepository {
    suspend fun getCycles(): List<CycleData>
    suspend fun createCycle(
        lastPeriodDate: String,
        cycleLength: Int,
        notes: String
    ): CycleData

    suspend fun addSymptom(
        cycleId: Int,
        date: String,
        mood: Int?,
        energy: Int?,
        flowIntensity: Int?,
        painLevel: Int?,
        notes: String?
    ): SymptomData
}