package com.herra_org.heraclient.domain.model.recommendation

data class Recommendation(
    val type: String,
    val title: String,
    val description: String,
    val priority: Int
)