package com.herra_org.heraclient.presentation.screens.cycle_tracking.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
internal fun CalendarDay(
    day: String,
    isSelected: Boolean,
    onDayClick: () -> Unit,
    hasSymptoms: Boolean,
    isCurrentPhase: Boolean
) {
    Box(
        modifier = Modifier
            .aspectRatio(1f)
            .padding(2.dp)
            .clip(CircleShape)
            .background(
                when {
                    isSelected -> MaterialTheme.colorScheme.primary
                    isCurrentPhase -> MaterialTheme.colorScheme.primaryContainer
                    else -> Color.Transparent
                }
            )
            .clickable(enabled = day.isNotEmpty(), onClick = onDayClick),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = day,
            style = MaterialTheme.typography.bodyMedium,
            color = when {
                isSelected -> MaterialTheme.colorScheme.onPrimary
                else -> MaterialTheme.colorScheme.onSurface
            }
        )
        if (hasSymptoms) {
            Box(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 4.dp)
                    .size(4.dp)
                    .background(MaterialTheme.colorScheme.primary, CircleShape)
            )
        }
    }
}