package com.herra_org.heraclient.presentation.screens.cycle_tracking.components.symptoms

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.BatteryChargingFull
import androidx.compose.material.icons.filled.Bedtime
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Face
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.LocalHospital
import androidx.compose.material.icons.filled.Opacity
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SymptomsSection(navController: NavController) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Text(
            text = "Log Symptoms",
            style = MaterialTheme.typography.titleLarge
        )

        Spacer(modifier = Modifier.height(16.dp))

        LazyVerticalGrid(
            columns = GridCells.Fixed(4),
            modifier = Modifier.height(180.dp),
            userScrollEnabled = false
        ) {
            items(
                listOf(
                    "Mood" to Icons.Default.Face,
                    "Flow" to Icons.Default.Opacity,
                    "Pain" to Icons.Default.LocalHospital,
                    "Sleep" to Icons.Default.Bedtime,
                    "Energy" to Icons.Default.BatteryChargingFull,
                    "Exercise" to Icons.Default.FitnessCenter,
                    "Notes" to Icons.Default.Edit,
                    "More" to Icons.Default.Add
                )
            ) { (label, icon) ->
                SymptomItem(
                    icon = icon,
                    label = label,
                    onClick = {
                        // Navigate to symptom logging screen
                    }
                )
            }
        }
    }
}
