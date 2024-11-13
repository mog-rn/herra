package com.herra_org.heraclient.domain.model.cycles

import android.os.Build
import androidx.annotation.RequiresApi
import com.herra_org.heraclient.data.remote.dto.cycles.CycleResponseDto
import kotlinx.coroutines.NonCancellable.isActive
import java.time.LocalDate
import java.time.temporal.ChronoUnit

data class Cycle(
    val id: Int,
    val startDate: LocalDate,
    val endDate: LocalDate?,
    val length: Int,
    val isActive: Boolean,
    val currentDay: Int,
    val daysRemaining: Int
)

data class CycleDetail(
    val id: Int,
    val startDate: LocalDate,
    val endDate: LocalDate?,
    val length: Int,
    val isActive: Boolean,
    val symptoms: List<Symptom>
)

data class Symptom(
    val id: Int,
    val cycleId: Int,
    val type: String,
    val severity: Int,
    val date: LocalDate,
    val notes: String?
)