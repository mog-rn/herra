package com.herra_org.heraclient.domain.use_cases.auth

import com.herra_org.heraclient.domain.model.auth.AuthResponse
import com.herra_org.heraclient.domain.model.auth.UserRegistration
import com.herra_org.heraclient.domain.repository.AuthRepository
import com.herra_org.heraclient.utils.Resource
import kotlinx.coroutines.flow.Flow

class RegisterUseCase(
    private val repository: AuthRepository
) {
    operator fun invoke(userRegistration: UserRegistration): Flow<Resource<AuthResponse>> {
        return repository.register(userRegistration)
    }
}