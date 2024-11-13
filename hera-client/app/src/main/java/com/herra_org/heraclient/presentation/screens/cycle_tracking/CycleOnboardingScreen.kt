package com.herra_org.heraclient.presentation.screens.cycle_tracking

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.herra_org.heraclient.presentation.view_models.cycle_tracking.CycleTrackingViewModel
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CycleOnboardingScreen(
    onComplete: () -> Unit,
    viewModel: CycleTrackingViewModel = hiltViewModel()
) {
    var lastPeriodDate by remember { mutableStateOf("") }
    var cycleLength by remember { mutableStateOf("28") }
    var notes by remember { mutableStateOf("") }
    var showDatePicker by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "Let's Set Up Your Cycle",
            style = MaterialTheme.typography.headlineMedium,
            textAlign = TextAlign.Center
        )

        OutlinedTextField(
            value = lastPeriodDate,
            onValueChange = { lastPeriodDate = it },
            label = { Text("Last Period Date (YYYY-MM-DD)") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        OutlinedTextField(
            value = cycleLength,
            onValueChange = {
                if (it.isEmpty() || it.toIntOrNull() != null) {
                    cycleLength = it
                }
            },
            label = { Text("Cycle Length (days)") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        OutlinedTextField(
            value = notes,
            onValueChange = { notes = it },
            label = { Text("Notes (optional)") },
            modifier = Modifier.fillMaxWidth(),
            minLines = 3,
            maxLines = 5
        )

        Button(
            onClick = {
//                viewModel.createInitialCycle(
//                    lastPeriodDate = lastPeriodDate,
//                    cycleLength = cycleLength.toIntOrNull() ?: 28,
//                    notes = notes
//                )
                onComplete()
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp),
            enabled = lastPeriodDate.isNotEmpty() && cycleLength.isNotEmpty()
        ) {
            Text("Start Tracking")
        }
    }

    if (showDatePicker) {
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(onClick = { showDatePicker = false }) {
                    Text("OK")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) {
                    Text("Cancel")
                }
            }
        ) {
            DatePicker(
                state = rememberDatePickerState(
                    initialSelectedDateMillis = System.currentTimeMillis()
                ),
//                onDismissRequest = { showDatePicker = false },
//                onDateSelected = { date ->
//                    lastPeriodDate = LocalDate.ofEpochDay(date / (24 * 60 * 60 * 1000))
//                        .format(DateTimeFormatter.ISO_LOCAL_DATE)
//                    showDatePicker = false
//                }
            )
        }
    }
}