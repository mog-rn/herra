package com.herra_org.heraclient.domain.use_cases.auth

import com.herra_org.heraclient.domain.model.auth.AuthResponse
import com.herra_org.heraclient.domain.model.auth.UserLogin
import com.herra_org.heraclient.domain.repository.AuthRepository
import com.herra_org.heraclient.utils.Resource
import kotlinx.coroutines.flow.Flow

class LoginUseCase(
    private val repository: AuthRepository
) {
    operator fun invoke(userLogin: UserLogin): Flow<Resource<AuthResponse>> {
        return repository.login(userLogin)
    }
}