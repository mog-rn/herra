package com.herra_org.heraclient.data.remote.dto.profile

import com.google.gson.annotations.SerializedName

data class ProfileResponseDto(
    @SerializedName("status") val status: String,
    @SerializedName("message") val message: String,
    @SerializedName("data") val data: ProfileDto
)

data class ProfileDto(
    @SerializedName("id") val id: Int,
    @SerializedName("email") val email: String,
    @SerializedName("firebase_uid") val firebaseUid: String,
    @SerializedName("first_name") val firstName: String,
    @SerializedName("last_name") val lastName: String,
    @SerializedName("is_active") val isActive: Boolean
)
