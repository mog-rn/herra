package com.herra_org.heraclient.presentation.view_models.cycle_tracking

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.herra_org.heraclient.domain.model.cycles.Cycle
import com.herra_org.heraclient.domain.model.cycles.CycleDetail
import com.herra_org.heraclient.domain.repository.HomeRepository
import com.herra_org.heraclient.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.YearMonth
import javax.inject.Inject

@HiltViewModel
@RequiresApi(Build.VERSION_CODES.O)
class CycleTrackingViewModel @Inject constructor(
    private val cycleRepository: HomeRepository
) : ViewModel() {

    private val _state = mutableStateOf(CycleTrackingState())
    val state: State<CycleTrackingState> = _state

    init {
        loadCurrentCycle()
    }

    private fun loadCurrentCycle() {
        viewModelScope.launch {
            try {
                _state.value = _state.value.copy(
                    isLoading = true,
                    error = null
                )

                cycleRepository.getCurrentCycle().collectLatest { result ->
                    when (result) {
                        is Resource.Success -> {
                            result.data?.let { cycle ->
                                _state.value = _state.value.copy(
                                    cycle = cycle,
                                    hasActiveCycle = true,
                                    isLoading = false,
                                    error = null
                                )
                                loadCycleDetails(cycle.id)
                            }
                        }
                        is Resource.Error -> {
                            _state.value = _state.value.copy(
                                hasActiveCycle = false,
                                isLoading = false,
                                error = result.message
                            )
                        }
                        is Resource.Loading -> {
                            _state.value = _state.value.copy(
                                isLoading = true
                            )
                        }
                    }
                }
            } catch (e: Exception) {
                _state.value = _state.value.copy(
                    error = e.message ?: "An unexpected error occurred",
                    isLoading = false
                )
            }
        }
    }

    private fun loadCycleDetails(cycleId: Int) {
        viewModelScope.launch {
            try {
                cycleRepository.getCycleDetails(cycleId).collectLatest { result ->
                    when (result) {
                        is Resource.Success -> {
                            _state.value = _state.value.copy(
                                cycleDetails = result.data,
                                isLoading = false,
                                error = null
                            )
                        }
                        is Resource.Error -> {
                            _state.value = _state.value.copy(
                                error = result.message,
                                isLoading = false
                            )
                        }
                        is Resource.Loading -> {
                            _state.value = _state.value.copy(
                                isLoading = true
                            )
                        }
                    }
                }
            } catch (e: Exception) {
                _state.value = _state.value.copy(
                    error = e.message ?: "An unexpected error occurred",
                    isLoading = false
                )
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun updateSelectedDate(date: LocalDate) {
        _state.value = _state.value.copy(selectedDate = date)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun updateMonth(isNext: Boolean) {
        val currentMonth = _state.value.currentYearMonth
        _state.value = _state.value.copy(
            currentYearMonth = if (isNext) {
                currentMonth.plusMonths(1)
            } else {
                currentMonth.minusMonths(1)
            }
        )
    }

    fun logSymptom(type: String, severity: Int = 1, notes: String? = null) {
        viewModelScope.launch {
            try {
                val cycleId = state.value.cycle?.id ?: return@launch

                _state.value = _state.value.copy(isLoading = true)

                cycleRepository.logSymptom(
                    cycleId = cycleId,
                    type = type,
                    severity = severity,
                    date = state.value.selectedDate,
                    notes = notes
                ).collectLatest { result ->
                    when (result) {
                        is Resource.Success -> {
                            loadCycleDetails(cycleId)
                        }
                        is Resource.Error -> {
                            _state.value = _state.value.copy(
                                error = result.message,
                                isLoading = false
                            )
                        }
                        is Resource.Loading -> {
                            _state.value = _state.value.copy(
                                isLoading = true
                            )
                        }
                    }
                }
            } catch (e: Exception) {
                _state.value = _state.value.copy(
                    error = e.message ?: "Failed to log symptom",
                    isLoading = false
                )
            }
        }
    }

    fun clearError() {
        _state.value = _state.value.copy(error = null)
    }

    fun refresh() {
        loadCurrentCycle()
    }
}

@RequiresApi(Build.VERSION_CODES.O)
data class CycleTrackingState constructor(
    val selectedDate: LocalDate = LocalDate.now(),
    val currentYearMonth: YearMonth = YearMonth.from(LocalDate.now()),
    val cycle: Cycle? = null,
    val cycleDetails: CycleDetail? = null,
    val hasActiveCycle: Boolean = false,
    val isLoading: Boolean = false,
    val error: String? = null
) {
    val showError: Boolean get() = error != null && !isLoading
    val showLoading: Boolean get() = isLoading
    val hasData: Boolean get() = cycle != null
}