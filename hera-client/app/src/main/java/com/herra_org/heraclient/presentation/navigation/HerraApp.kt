package com.herra_org.heraclient.presentation.navigation

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.getValue
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import com.herra_org.heraclient.presentation.components.SetStatusBarColor
import com.herra_org.heraclient.presentation.theme.HerraColors.Purple
import com.herra_org.heraclient.utils.BottomNavConfig

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun HerraApp() {
    val navController = rememberNavController()

    SetStatusBarColor(color = Purple, darkIcons = false)

    val bottomNavItems = listOf(
        BottomNavConfig.Dashboard,
//        BottomNavConfig.Transactions,
        BottomNavConfig.CycleTracking,
        BottomNavConfig.Wellness,
        BottomNavConfig.Profile
    )

    val bottomNavRoutes = bottomNavItems.map { it.route }

    val currentBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = currentBackStackEntry?.destination?.route

    Scaffold(
        bottomBar = {
            if (currentDestination in bottomNavRoutes) {
                BottomNavBar(navController, bottomNavItems)
            }
        },
        content = {paddingValues ->
            Box(modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(MaterialTheme.colorScheme.background)
            ) {
                NavHost(
                    navController = navController,
                    graph = navGraph(
                        navController,
//                        onScreenNameChange = { newScreenName ->
//                            screenName = newScreenName
//                        },
//                        isUserLoggedIn = isLoggedIn
                    )
                )
            }
        }
    )
}