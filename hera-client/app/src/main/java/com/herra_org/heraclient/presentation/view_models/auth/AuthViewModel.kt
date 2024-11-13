package com.herra_org.heraclient.presentation.view_models.auth

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import com.herra_org.heraclient.data.local.TokenManager
import com.herra_org.heraclient.domain.model.auth.AuthResponse
import com.herra_org.heraclient.domain.model.auth.UserLogin
import com.herra_org.heraclient.domain.model.auth.UserRegistration
import com.herra_org.heraclient.domain.use_cases.auth.LoginUseCase
import com.herra_org.heraclient.domain.use_cases.auth.RegisterUseCase
import com.herra_org.heraclient.utils.NavGraphs
import com.herra_org.heraclient.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val registerUseCase: RegisterUseCase,
    private val loginUseCase: LoginUseCase,
    private val tokenManager: TokenManager
) : ViewModel() {

    companion object {
        private const val TAG = "AuthViewModel"
    }

    private val _state = mutableStateOf(AuthState())
    val state: State<AuthState> = _state

    private val _eventFlow = MutableSharedFlow<UIEvent>()
    val eventFlow = _eventFlow.asSharedFlow()

    init {
        checkAuthStatus()
    }

    fun clearError() {
        _state.value = state.value.copy(error = "")
    }

    fun setError(message: String) {
        _state.value = state.value.copy(error = message)
    }


    private fun checkAuthStatus() {
        viewModelScope.launch {
            if (tokenManager.isTokenValid()) {
                _eventFlow.emit(UIEvent.NavigateToHome)
            }
        }
    }

    fun onEvent(event: AuthEvent) {
        when (event) {
            is AuthEvent.Register -> {
                register(
                    UserRegistration(
                        email = event.email,
                        password = event.password,
                        firstName = event.firstName,
                        lastName = event.lastName
                    )
                )
            }
            is AuthEvent.Login -> {
                login(
                    UserLogin(
                        email = event.email,
                        password = event.password
                    )
                )
            }
            is AuthEvent.Logout -> {
                logout()
            }
        }
    }

    private fun register(userRegistration: UserRegistration) {
        viewModelScope.launch {
            registerUseCase(userRegistration).onEach { result ->
                when (result) {
                    is Resource.Success -> {
                        _state.value = state.value.copy(
                            isLoading = false,
                            authResponse = result.data,
                            error = ""
                        )
                        result.data?.customToken?.let {
                            Timber.tag(TAG).d("Custom token received")
                        }
                        _eventFlow.emit(UIEvent.NavigateToLogin)
                    }
                    is Resource.Error -> {
                        _state.value = state.value.copy(
                            isLoading = false,
                            error = result.message ?: "An unexpected error occurred"
                        )
                    }
                    is Resource.Loading -> {
                        _state.value = state.value.copy(
                            isLoading = true,
                            error = ""
                        )
                    }
                }
            }.launchIn(viewModelScope)
        }
    }

    private fun login(userLogin: UserLogin) {
        viewModelScope.launch {
            loginUseCase(userLogin).onEach { result ->
                when (result) {
                    is Resource.Success -> {
                        result.data?.tokens?.let { tokens ->
                            tokenManager.saveTokens(
                                accessToken = tokens.accessToken,
                                refreshToken = tokens.refreshToken,
                                expiresIn = tokens.expiresIn
                            )
                        }
                        _state.value = state.value.copy(
                            isLoading = false,
                            authResponse = result.data,
                            error = ""
                        )
                        _eventFlow.emit(UIEvent.NavigateToHome)
                    }
                    is Resource.Error -> {
                        _state.value = state.value.copy(
                            isLoading = false,
                            error = result.message ?: "An unexpected error occurred"
                        )
                    }
                    is Resource.Loading -> {
                        _state.value = state.value.copy(
                            isLoading = true,
                            error = ""
                        )
                    }
                }
            }.launchIn(viewModelScope)
        }
    }

    private fun logout() {
        viewModelScope.launch {
            tokenManager.clearTokens()
            _state.value = AuthState() // Reset state
            _eventFlow.emit(UIEvent.NavigateToLogin)
        }
    }
}

data class AuthState(
    val isLoading: Boolean = false,
    val authResponse: AuthResponse? = null,
    val error: String = ""
)

sealed class AuthEvent {
    data class Register(
        val email: String,
        val password: String,
        val firstName: String,
        val lastName: String
    ) : AuthEvent()

    data class Login(
        val email: String,
        val password: String
    ) : AuthEvent()

    object Logout : AuthEvent()
}

sealed class UIEvent {
    object NavigateToHome : UIEvent()
    object NavigateToLogin : UIEvent()
}

fun NavController.navigateToMain() {
    navigate(NavGraphs.MAIN) {
        popUpTo(NavGraphs.AUTH) {
            inclusive = true
        }
    }
}