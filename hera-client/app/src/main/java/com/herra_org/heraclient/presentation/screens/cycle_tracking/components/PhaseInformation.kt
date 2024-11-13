package com.herra_org.heraclient.presentation.screens.cycle_tracking.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.herra_org.heraclient.domain.model.cycles.CyclePhase

@Composable
fun PhaseInformation(
    currentPhase: CyclePhase,
    cycleLength: Int,
    daysIntoCycle: Int
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.secondaryContainer
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "Current Phase",
                style = MaterialTheme.typography.titleMedium
            )
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = getPhaseName(currentPhase),
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = "Day $daysIntoCycle of $cycleLength",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
                CircularProgressIndicator(
                    progress = daysIntoCycle.toFloat() / cycleLength,
                    modifier = Modifier.size(48.dp),
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}

private fun getPhaseName(phase: CyclePhase): String {
    return when (phase) {
        CyclePhase.MENSTRUAL -> "Menstrual Phase"
        CyclePhase.FOLLICULAR -> "Follicular Phase"
        CyclePhase.OVULATION -> "Ovulation Phase"
        CyclePhase.LUTEAL -> "Luteal Phase"
    }
}
