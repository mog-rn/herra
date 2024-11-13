package com.herra_org.heraclient.domain.repository

import com.herra_org.heraclient.domain.model.profile.Profile
import com.herra_org.heraclient.utils.Resource
import kotlinx.coroutines.flow.Flow

interface ProfileRepository {
    suspend fun getProfile(): Flow<Resource<Profile>>
}