package com.herra_org.heraclient.domain.model.cycles

import android.os.Build
import androidx.annotation.RequiresApi
import com.herra_org.heraclient.data.remote.dto.cycles.*
import java.time.LocalDate
import java.time.temporal.ChronoUnit

@RequiresApi(Build.VERSION_CODES.O)
fun CycleResponseDto.toDomain(): Cycle {
    val start = LocalDate.parse(startDate)
    val end = endDate?.let { LocalDate.parse(it) }
    val today = LocalDate.now()
    val currentDay = ChronoUnit.DAYS.between(start, today).toInt() + 1
    val daysRemaining = length - currentDay

    return Cycle(
        id = id,
        startDate = start,
        endDate = end,
        length = length,
        isActive = isActive,
        currentDay = currentDay,
        daysRemaining = daysRemaining
    )
}

@RequiresApi(Build.VERSION_CODES.O)
fun CycleDetailResponseDto.toDomain(): CycleDetail {
    return CycleDetail(
        id = id,
        startDate = LocalDate.parse(startDate),
        endDate = endDate?.let { LocalDate.parse(it) },
        length = length,
        isActive = isActive,
        symptoms = symptoms.map { it.toDomain() }
    )
}

@RequiresApi(Build.VERSION_CODES.O)
fun SymptomDto.toDomain(): Symptom {
    return Symptom(
        id = id,
        cycleId = cycleId,
        type = type,
        severity = severity,
        date = LocalDate.parse(date),
        notes = notes
    )
}

@RequiresApi(Build.VERSION_CODES.O)
fun SymptomLogResponseDto.toDomain(): Symptom {
    return Symptom(
        id = id,
        cycleId = cycleId,
        type = type,
        severity = severity,
        date = LocalDate.parse(date),
        notes = notes
    )
}