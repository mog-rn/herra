package com.herra_org.heraclient.utils

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.ui.graphics.vector.ImageVector

// Normal Screens Configuration
sealed class RouteConfig(val route: String) {
    // Auth Flow
//    data object Splash : RouteConfig("splash")
//    data object Welcome : RouteConfig("welcome")
    data object Login : RouteConfig("login")
    data object Register : RouteConfig("register")
    data object ForgotPassword : RouteConfig("forgot_password")

    // Onboarding Flow
    data object OnboardingWelcome : RouteConfig("onboarding_welcome")
    data object CycleSetup : RouteConfig("cycle_setup")
    data object EmergencyContactsSetup : RouteConfig("emergency_contacts_setup")

    // Main Flow Screens
    data object CycleCalendar : RouteConfig("cycle_calendar")
    data object SymptomLog : RouteConfig("symptom_log")
    data object CycleHistory : RouteConfig("cycle_history")
    data object EmergencyTimer : RouteConfig("emergency_timer")
    data object EmergencyContacts : RouteConfig("emergency_contacts")

    // Profile Related Screens
    data object EditProfile : RouteConfig("edit_profile")
    data object Settings : RouteConfig("settings")
    data object Notifications : RouteConfig("notifications")
    data object Privacy : RouteConfig("privacy")
    data object Help : RouteConfig("help")

    // Additional Utility Screens
    data object ScanScreen : RouteConfig("scan")
    data object DocumentViewer : RouteConfig("document_viewer")
}

// Bottom Tabs Configuration
sealed class BottomNavConfig(
    val route: String,
    val title: String,
    val icon: ImageVector,
    val selectedIcon: ImageVector = icon
) {
    data object Dashboard: BottomNavConfig(
        route = "dashboard",
        title = "Home",
        icon = Icons.Default.SpaceDashboard,
        selectedIcon = Icons.Filled.SpaceDashboard
    )

    data object CycleTracking: BottomNavConfig(
        route = "cycle_tracking",
        title = "Cycle",
        icon = Icons.Default.CalendarToday,
        selectedIcon = Icons.Filled.CalendarToday
    )

    data object Wellness: BottomNavConfig(
        route = "wellness",
        title = "Wellness",
        icon = Icons.Default.Spa,
        selectedIcon = Icons.Filled.Spa
    )

    data object Profile: BottomNavConfig(
        route = "profile",
        title = "Profile",
        icon = Icons.Default.Person,
        selectedIcon = Icons.Filled.Person
    )

    companion object {
        fun bottomNavItems() = listOf(
            Dashboard,
            CycleTracking,
            Wellness,
            Profile
        )
    }
}

// Navigation Graph Routes
object NavGraphs {
    const val AUTH = "auth_graph"
    const val ONBOARDING = "onboarding_graph"
    const val MAIN = "main_graph"
    const val ROOT = "root_graph"
}

// Screen Arguments
object NavArgs {
    const val USER_ID = "userId"
    const val CYCLE_DATE = "cycleDate"
    const val SYMPTOM_TYPE = "symptomType"
    const val EMERGENCY_CONTACT_ID = "emergencyContactId"
}