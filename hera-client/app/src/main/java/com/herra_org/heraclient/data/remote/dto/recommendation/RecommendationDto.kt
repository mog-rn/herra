package com.herra_org.heraclient.data.remote.dto.recommendation

import com.google.gson.annotations.SerializedName

data class RecommendationDto(
    @SerializedName("type") val type: String,
    @SerializedName("title") val title: String,
    @SerializedName("description") val description: String,
    @SerializedName("priority") val priority: Int
)

data class RecommendationsResponseDto(
    @SerializedName("date") val date: String,
    @SerializedName("recommendations") val recommendations: List<RecommendationDto>
)