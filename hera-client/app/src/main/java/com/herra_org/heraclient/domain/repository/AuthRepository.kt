package com.herra_org.heraclient.domain.repository

import com.herra_org.heraclient.domain.model.auth.AuthResponse
import com.herra_org.heraclient.domain.model.auth.UserLogin
import com.herra_org.heraclient.domain.model.auth.UserRegistration
import com.herra_org.heraclient.utils.Resource
import kotlinx.coroutines.flow.Flow

interface AuthRepository {
    fun register(userRegistration: UserRegistration): Flow<Resource<AuthResponse>>
    fun login(userLogin: UserLogin): Flow<Resource<AuthResponse>>
}