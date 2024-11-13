package com.herra_org.heraclient.presentation.screens.cycle_tracking.components

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.herra_org.heraclient.presentation.screens.cycle_tracking.CycleGridData
import java.time.LocalDate
import java.time.YearMonth

data class CycleGridData(
    val startDate: LocalDate?,
    val endDate: LocalDate?,
//    val symptoms: List<DaySymptom>,
    val isActive: Boolean
)

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun CalendarGrid(
    yearMonth: YearMonth,
    selectedDate: LocalDate,
    onDateSelected: (LocalDate) -> Unit,
    cycleData: CycleGridData
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
    ) {
        // Weekday headers
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            listOf("M", "T", "W", "T", "F", "S", "S").forEach { day ->
                Text(
                    text = day,
                    modifier = Modifier.weight(1f),
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Calendar days
        val firstDayOfMonth = yearMonth.atDay(1)
        val daysInMonth = yearMonth.lengthOfMonth()

        // Calculate the day of week offset (1-7, where 1 is Monday)
        val firstDayOfWeek = firstDayOfMonth.dayOfWeek.value

        // Calculate total days to display (including empty spaces)
        val totalDays = firstDayOfWeek - 1 + daysInMonth
        val totalWeeks = (totalDays + 6) / 7

        LazyVerticalGrid(
            columns = GridCells.Fixed(7),
            modifier = Modifier.height((totalWeeks * 48).dp),
            userScrollEnabled = false
        ) {
            // Empty cells for days before the first of the month
            items(firstDayOfWeek - 1) {
                CalendarDay(
                    day = "",
                    isSelected = false,
                    onDayClick = {},
                    hasSymptoms = false,
                    isCurrentPhase = false
                )
            }

            // Actual days of the month
            items(daysInMonth) { day ->
                val date = yearMonth.atDay(day + 1)
                CalendarDay(
                    day = (day + 1).toString(),
                    isSelected = date == selectedDate,
                    onDayClick = { onDateSelected(date) },
                    hasSymptoms = false, // Replace with actual data
                    isCurrentPhase = date.month == selectedDate.month
                )
            }
        }
    }
}