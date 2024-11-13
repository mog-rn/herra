package com.herra_org.heraclient.data.remote.requests

data class AddSymptomRequest(
    val date: String,
    val mood: Int?,
    val energy: Int?,
    val flowIntensity: Int?,
    val painLevel: Int?,
    val notes: String?
)