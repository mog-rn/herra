package com.herra_org.heraclient.presentation.navigation

import HomeScreen
import android.annotation.SuppressLint
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.input.key.Key.Companion.Home
import androidx.navigation.NavController
import androidx.navigation.NavGraph
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.createGraph
import androidx.navigation.navArgument
import androidx.navigation.navigation
import com.herra_org.heraclient.presentation.screens.auth.ForgotPasswordScreen
import com.herra_org.heraclient.presentation.screens.auth.LoginScreen
import com.herra_org.heraclient.presentation.screens.auth.RegisterScreen
import com.herra_org.heraclient.presentation.screens.cycle_tracking.CycleCalendarScreen
import com.herra_org.heraclient.presentation.screens.cycle_tracking.CycleHistoryScreen
import com.herra_org.heraclient.presentation.screens.cycle_tracking.CycleTracking
import com.herra_org.heraclient.presentation.screens.cycle_tracking.SymptomLogScreen
import com.herra_org.heraclient.presentation.screens.onboarding.CycleSetupScreen
import com.herra_org.heraclient.presentation.screens.onboarding.EmergencyContactsSetupScreen
import com.herra_org.heraclient.presentation.screens.onboarding.OnboardingWelcomeScreen
import com.herra_org.heraclient.presentation.screens.profile.EditProfileScreen
import com.herra_org.heraclient.presentation.screens.profile.HelpScreen
import com.herra_org.heraclient.presentation.screens.profile.NotificationsScreen
import com.herra_org.heraclient.presentation.screens.profile.PrivacyScreen
import com.herra_org.heraclient.presentation.screens.profile.SettingsScreen
import com.herra_org.heraclient.presentation.screens.profile.UserProfile
import com.herra_org.heraclient.presentation.screens.wellness.EmergencyContactsScreen
import com.herra_org.heraclient.presentation.screens.wellness.EmergencyTimerScreen
import com.herra_org.heraclient.presentation.screens.wellness.Wellness
import com.herra_org.heraclient.utils.BottomNavConfig
import com.herra_org.heraclient.utils.NavArgs
import com.herra_org.heraclient.utils.NavGraphs
import com.herra_org.heraclient.utils.RouteConfig

@RequiresApi(Build.VERSION_CODES.O)
@SuppressLint("RememberReturnType")
@Composable
fun navGraph(
    navController: NavController
): NavGraph {
    val navGraph = remember(navController) {
        navController.createGraph(startDestination = NavGraphs.AUTH) {
            // Auth Flow
            navigation(
                startDestination = RouteConfig.Login.route,
                route = NavGraphs.AUTH
            ) {
//                composable(RouteConfig.Splash.route) {
//                    SplashScreen(navController)
//                }
//                composable(RouteConfig.Welcome.route) {
//                    WelcomeScreen(navController)
//                }
                composable(RouteConfig.Login.route) {
                    LoginScreen(navController)
                }
                composable(RouteConfig.Register.route) {
                    RegisterScreen(navController)
                }
                composable(RouteConfig.ForgotPassword.route) {
                    ForgotPasswordScreen(navController)
                }
            }

            // Onboarding Flow
            navigation(
                startDestination = RouteConfig.OnboardingWelcome.route,
                route = NavGraphs.ONBOARDING
            ) {
                composable(RouteConfig.OnboardingWelcome.route) {
                    OnboardingWelcomeScreen(navController)
                }
                composable(RouteConfig.CycleSetup.route) {
                    CycleSetupScreen(navController)
                }
                composable(RouteConfig.EmergencyContactsSetup.route) {
                    EmergencyContactsSetupScreen(navController)
                }
            }

            // Main Flow with Bottom Navigation
            navigation(
                startDestination = BottomNavConfig.Dashboard.route,
                route = NavGraphs.MAIN
            ) {
                // Dashboard/Home
                composable(BottomNavConfig.Dashboard.route) {
                    HomeScreen(navController)
                }

                // Cycle Tracking Flow
                composable(BottomNavConfig.CycleTracking.route) {
                    CycleTracking(navController)
                }
                composable(RouteConfig.CycleCalendar.route) {
                    CycleCalendarScreen(navController)
                }
                composable(
                    route = "${RouteConfig.SymptomLog.route}/{${NavArgs.CYCLE_DATE}}",
                    arguments = listOf(
                        navArgument(NavArgs.CYCLE_DATE) { type = NavType.StringType }
                    )
                ) { backStackEntry ->
                    SymptomLogScreen(
                        navController = navController,
                        date = backStackEntry.arguments?.getString(NavArgs.CYCLE_DATE) ?: ""
                    )
                }
                composable(RouteConfig.CycleHistory.route) {
                    CycleHistoryScreen(navController)
                }

                // Wellness Flow
                composable(BottomNavConfig.Wellness.route) {
                    Wellness(navController)
                }
                composable(RouteConfig.EmergencyTimer.route) {
                    EmergencyTimerScreen(navController)
                }
                composable(RouteConfig.EmergencyContacts.route) {
                    EmergencyContactsScreen(navController)
                }

                // Profile Flow
                composable(BottomNavConfig.Profile.route) {
                    UserProfile(navController)
                }
                composable(RouteConfig.EditProfile.route) {
                    EditProfileScreen(navController)
                }
                composable(RouteConfig.Settings.route) {
                    SettingsScreen(navController)
                }
                composable(RouteConfig.Notifications.route) {
                    NotificationsScreen(navController)
                }
                composable(RouteConfig.Privacy.route) {
                    PrivacyScreen(navController)
                }
                composable(RouteConfig.Help.route) {
                    HelpScreen(navController)
                }
            }
        }
    }

    return navGraph
}

// Navigation Extensions
fun NavController.navigateToMain() {
    navigate(NavGraphs.MAIN) {
        popUpTo(NavGraphs.AUTH) { inclusive = true }
    }
}

fun NavController.navigateToOnboarding() {
    navigate(NavGraphs.ONBOARDING) {
        popUpTo(NavGraphs.AUTH) { inclusive = true }
    }
}

fun NavController.navigateToSymptomLog(date: String) {
    navigate("${RouteConfig.SymptomLog.route}/$date")
}

fun NavController.navigateToEmergencyContacts() {
    navigate(RouteConfig.EmergencyContacts.route)
}

fun NavController.navigateToSettings() {
    navigate(RouteConfig.Settings.route)
}

fun NavController.navigateToEditProfile() {
    navigate(RouteConfig.EditProfile.route)
}

fun NavController.signOut() {
    navigate(NavGraphs.AUTH) {
        popUpTo(NavGraphs.MAIN) { inclusive = true }
    }
}