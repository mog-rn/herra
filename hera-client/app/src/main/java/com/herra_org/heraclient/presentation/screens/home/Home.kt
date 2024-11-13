import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bedtime
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.herra_org.heraclient.presentation.components.LoadingDialog
import com.herra_org.heraclient.presentation.screens.home.components.*
import com.herra_org.heraclient.presentation.view_models.home.HomeViewModel
import com.herra_org.heraclient.utils.RouteConfig

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun HomeScreen(
    navController: NavController,
    viewModel: HomeViewModel = hiltViewModel()
) {
    val state = viewModel.state.value
    val scrollState = rememberScrollState()

    LaunchedEffect(key1 = true) {
        viewModel.loadHomeData()
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .verticalScroll(scrollState)
        ) {
            HomeHeader(
                userName = state.userName,
                onNotificationClick = { /* Handle notifications */ }
            )

            state.cycle?.let { cycle ->
                CycleOverviewCard(
                    currentDay = cycle.currentDay,
                    cycleLength = cycle.length,
                    phase = "Follicular Phase", // This should come from phase calculation
                    progress = cycle.currentDay.toFloat() / cycle.length,
                    onDetailsClick = {
                        navController.navigate(RouteConfig.CycleCalendar.route)
                    }
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            WellnessTimerCard(
                onTimerActivate = {
                    navController.navigate(RouteConfig.EmergencyTimer.route)
                }
            )

            Spacer(modifier = Modifier.height(16.dp))

            RecommendationsCard(
                recommendations = listOf(
                    RecommendationItem(
                        icon = Icons.Default.FitnessCenter,
                        title = "Exercises",
                        description = "Light cardio recommended for your current phase"
                    ),
                    RecommendationItem(
                        icon = Icons.Default.Restaurant,
                        title = "Nutrition",
                        description = "Focus on iron-rich foods today"
                    ),
                    RecommendationItem(
                        icon = Icons.Default.Bedtime,
                        title = "Rest",
                        description = "Try to get 8 hours of sleep tonight"
                    )
                )
            )

            Spacer(modifier = Modifier.height(16.dp))

            QuickActions(
                onLogSymptoms = {
                    state.cycle?.id?.let { cycleId ->
                        navController.navigate("${RouteConfig.SymptomLog.route}/$cycleId")
                    }
                },
                onViewCalendar = {
                    navController.navigate(RouteConfig.CycleCalendar.route)
                }
            )

            Spacer(modifier = Modifier.height(32.dp))
        }

        // Error handling
        if (state.showError) {
            Snackbar(
                modifier = Modifier
                    .padding(16.dp)
                    .align(Alignment.BottomCenter),
                action = {
                    TextButton(onClick = { viewModel.clearError() }) {
                        Text("Dismiss")
                    }
                }
            ) {
                Text(state.error ?: "Unknown error occurred")
            }
        }

        // Loading state
        if (state.showLoading) {
            LoadingDialog()
        }
    }
}