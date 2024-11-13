package com.herra_org.heraclient.data.remote.dto.cycles

import com.google.gson.annotations.SerializedName

data class PhaseResponseDto(
    @SerializedName("phase_name") val phaseName: String,
    @SerializedName("start_day") val startDay: Int,
    @SerializedName("end_day") val endDay: Int,
    @SerializedName("current_day") val currentDay: Int
)

data class CycleResponseDto(
    @SerializedName("id") val id: Int,
    @SerializedName("user") val userId: Int,
    @SerializedName("start_date") val startDate: String,
    @SerializedName("end_date") val endDate: String?,
    @SerializedName("length") val length: Int,
    @SerializedName("is_active") val isActive: Boolean
)

data class CycleDetailResponseDto(
    @SerializedName("id") val id: Int,
    @SerializedName("user") val userId: Int,
    @SerializedName("start_date") val startDate: String,
    @SerializedName("end_date") val endDate: String?,
    @SerializedName("length") val length: Int,
    @SerializedName("is_active") val isActive: Boolean,
    @SerializedName("symptoms") val symptoms: List<SymptomDto>
)

data class SymptomDto(
    @SerializedName("id") val id: Int,
    @SerializedName("cycle") val cycleId: Int,
    @SerializedName("type") val type: String,
    @SerializedName("severity") val severity: Int,
    @SerializedName("date") val date: String,
    @SerializedName("notes") val notes: String?
)

data class SymptomLogRequestDto(
    @SerializedName("cycle") val cycleId: Int,
    @SerializedName("type") val type: String,
    @SerializedName("severity") val severity: Int,
    @SerializedName("date") val date: String,
    @SerializedName("notes") val notes: String? = null
)

data class SymptomLogResponseDto(
    @SerializedName("id") val id: Int,
    @SerializedName("cycle") val cycleId: Int,
    @SerializedName("type") val type: String,
    @SerializedName("severity") val severity: Int,
    @SerializedName("date") val date: String,
    @SerializedName("notes") val notes: String?
)