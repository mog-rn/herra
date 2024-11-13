package com.herra_org.heraclient.domain.model.profile

import com.herra_org.heraclient.data.remote.dto.profile.ProfileDto

data class Profile(
    val id: Int,
    val email: String,
    val firebaseUid: String,
    val firstName: String,
    val lastName: String,
    val isActive: Boolean
)

// Extensions
fun ProfileDto.toDomain() = Profile(
    id = id,
    email = email,
    firebaseUid = firebaseUid,
    firstName = firstName,
    lastName = lastName,
    isActive = isActive
)