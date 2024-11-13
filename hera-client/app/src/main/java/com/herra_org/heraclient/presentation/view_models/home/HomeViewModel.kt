package com.herra_org.heraclient.presentation.view_models.home

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.herra_org.heraclient.domain.model.cycles.Cycle
import com.herra_org.heraclient.domain.model.cycles.CycleDetail
import com.herra_org.heraclient.domain.model.profile.Profile
import com.herra_org.heraclient.domain.repository.HomeRepository
import com.herra_org.heraclient.domain.repository.ProfileRepository
import com.herra_org.heraclient.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val homeRepository: HomeRepository,
    private val profileRepository: ProfileRepository
) : ViewModel() {

    private val _state = mutableStateOf(HomeState())
    val state: State<HomeState> = _state

    private var currentCycleId: Int? = null

    init {
        loadHomeData()
        loadProfile()
    }

    fun loadHomeData() {
        viewModelScope.launch {
            try {
                _state.value = state.value.copy(
                    isLoading = true,
                    error = null
                )

                homeRepository.getCurrentCycle().collect { result ->
                    when (result) {
                        is Resource.Success -> {
                            result.data?.let { cycle ->
                                currentCycleId = cycle.id
                                _state.value = state.value.copy(
                                    cycle = cycle,
                                    error = null,
                                    isLoading = false
                                )
                                loadCycleDetails(cycle.id)
                            }
                        }

                        is Resource.Error -> {
                            _state.value = state.value.copy(
                                error = result.message,
                                isLoading = false
                            )
                        }

                        is Resource.Loading -> {
                            _state.value = state.value.copy(
                                isLoading = true
                            )
                        }
                    }
                }
            } catch (e: Exception) {
                _state.value = state.value.copy(
                    error = e.localizedMessage ?: "An unexpected error occurred",
                    isLoading = false
                )
            }
        }
    }

    private fun loadCycleDetails(cycleId: Int) {
        viewModelScope.launch {
            try {
                homeRepository.getCycleDetails(cycleId).collect { result ->
                    when (result) {
                        is Resource.Success -> {
                            _state.value = state.value.copy(
                                cycleDetails = result.data,
                                error = null,
                                isLoading = false
                            )
                        }

                        is Resource.Error -> {
                            _state.value = state.value.copy(
                                error = result.message,
                                isLoading = false
                            )
                        }

                        is Resource.Loading -> {
                            _state.value = state.value.copy(
                                isLoading = true
                            )
                        }
                    }
                }
            } catch (e: Exception) {
                _state.value = state.value.copy(
                    error = e.localizedMessage ?: "An unexpected error occurred",
                    isLoading = false
                )
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun logSymptom(type: String, severity: Int, notes: String? = null) {
        viewModelScope.launch {
            try {
                currentCycleId?.let { cycleId ->
                    homeRepository.logSymptom(
                        cycleId = cycleId,
                        type = type,
                        severity = severity,
                        date = LocalDate.now(),
                        notes = notes
                    ).collect { result ->
                        when (result) {
                            is Resource.Success -> {
                                loadCycleDetails(cycleId)
                            }

                            is Resource.Error -> {
                                _state.value = state.value.copy(
                                    error = result.message,
                                    isLoading = false
                                )
                            }

                            is Resource.Loading -> {
                                _state.value = state.value.copy(
                                    isLoading = true
                                )
                            }
                        }
                    }
                }
            } catch (e: Exception) {
                _state.value = state.value.copy(
                    error = e.localizedMessage ?: "An unexpected error occurred",
                    isLoading = false
                )
            }
        }
    }

    private fun loadProfile() {
        viewModelScope.launch {
            profileRepository.getProfile()
                .collect { result ->
                    when (result) {
                        is Resource.Success -> {
                            _state.value = state.value.copy(
                                profile = result.data,
                                error = null
                            )
                        }

                        is Resource.Error -> {
                            _state.value = state.value.copy(
                                error = result.message,
                                isLoading = false
                            )
                        }

                        is Resource.Loading -> {
                            _state.value = state.value.copy(
                                isLoading = true
                            )
                        }
                    }
                }
        }
    }


    fun clearError() {
        _state.value = state.value.copy(error = null)
    }

    fun refresh() {
        loadHomeData()
    }
}

data class HomeState(
    val cycle: Cycle? = null,
    val cycleDetails: CycleDetail? = null,
    val isLoading: Boolean = false,
    val error: String? = null,
    val profile: Profile? = null,
) {
    val showError: Boolean get() = error != null && !isLoading
    val showLoading: Boolean get() = isLoading
    val hasData: Boolean get() = cycle != null && cycleDetails != null
    val userName: String get() = profile?.firstName?.takeIf { it.isNotBlank() }
        ?: profile?.email?.substringBefore('@')
        ?: "User"
}

sealed class CycleTrackingState {
    object Loading : CycleTrackingState()
    object HasActiveCycle : CycleTrackingState()
    object NoCycle : CycleTrackingState()
    data class Error(val message: String) : CycleTrackingState()
}