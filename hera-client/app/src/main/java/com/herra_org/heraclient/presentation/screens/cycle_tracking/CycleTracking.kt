package com.herra_org.heraclient.presentation.screens.cycle_tracking

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.herra_org.heraclient.domain.model.cycles.Symptom
import com.herra_org.heraclient.presentation.components.LoadingDialog
import com.herra_org.heraclient.presentation.screens.cycle_tracking.components.*
import com.herra_org.heraclient.presentation.screens.cycle_tracking.components.symptoms.SymptomsSection
import com.herra_org.heraclient.presentation.view_models.cycle_tracking.CycleTrackingState
import com.herra_org.heraclient.presentation.view_models.cycle_tracking.CycleTrackingViewModel
import java.time.LocalDate

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun CycleTracking(
    navController: NavController,
    viewModel: CycleTrackingViewModel = hiltViewModel()
) {
    val state by viewModel.state

    Box(modifier = Modifier.fillMaxSize()) {
        when {
            state.isLoading -> {
                LoadingScreen()
            }
            state.error != null -> {
                ErrorScreen(
                    message = state.error ?: "An error occurred",
                    onRetry = { viewModel.refresh() }
                )
            }
            !state.hasActiveCycle -> {
                CycleOnboardingScreen(
                    onComplete = { viewModel.refresh() }
                )
            }
            else -> {
                CycleTrackingContent(
                    state = state,
                    onPreviousMonth = { viewModel.updateMonth(false) },
                    onNextMonth = { viewModel.updateMonth(true) },
                    onDateSelected = { viewModel.updateSelectedDate(it) },
                    onLogSymptom = { type, severity, notes ->
                        viewModel.logSymptom(type, severity, notes)
                    }
                )
            }
        }

        // Error Snackbar
        if (state.showError) {
            Snackbar(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(16.dp),
                action = {
                    TextButton(onClick = { viewModel.clearError() }) {
                        Text("Dismiss")
                    }
                }
            ) {
                Text(state.error ?: "An unexpected error occurred")
            }
        }

        // Loading Dialog
        if (state.showLoading) {
            LoadingDialog()
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
private fun CycleTrackingContent(
    state: CycleTrackingState,
    onPreviousMonth: () -> Unit,
    onNextMonth: () -> Unit,
    onDateSelected: (LocalDate) -> Unit,
    onLogSymptom: (String, Int, String?) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        CycleTrackingTopBar(
            currentYearMonth = state.currentYearMonth,
            onPreviousMonth = onPreviousMonth,
            onNextMonth = onNextMonth
        )

        // Calendar Section
        state.cycle?.let { cycle ->
            CalendarGrid(
                yearMonth = state.currentYearMonth,
                selectedDate = state.selectedDate,
                onDateSelected = onDateSelected,
                cycleData = CycleGridData(
                    startDate = cycle.startDate,
                    endDate = cycle.endDate,
                    symptoms = state.cycleDetails?.symptoms.orEmpty()
                )
            )

            // Phase Information
//            PhaseInformation(
////                currentDay = cycle.currentDay,
////                totalDays = cycle.length,
////                isActive = cycle.isActive
//            )
        }

        // Symptoms Section
//        SymptomsSection(
//            selectedDate = state.selectedDate,
//            onSymptomLog = { symptomType ->
//                onLogSymptom(symptomType, 1, null)
//            },
//            navController = TODO()
//        )
    }
}

data class CycleGridData(
    val startDate: LocalDate,
    val endDate: LocalDate?,
    val symptoms: List<Symptom>
)

// Supporting Components
@Composable
private fun LoadingScreen() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator()
    }
}

@Composable
private fun ErrorScreen(
    message: String,
    onRetry: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = message,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.error
        )
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = onRetry) {
            Text("Retry")
        }
    }
}

@Composable
private fun CycleOnboardingScreen(
    onComplete: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Welcome to Cycle Tracking",
            style = MaterialTheme.typography.headlineMedium
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "Let's set up your first cycle",
            style = MaterialTheme.typography.bodyLarge
        )
        Spacer(modifier = Modifier.height(32.dp))
        Button(onClick = onComplete) {
            Text("Get Started")
        }
    }
}