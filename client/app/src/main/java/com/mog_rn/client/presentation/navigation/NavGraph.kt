package com.mog_rn.client.presentation.navigation

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.navigation.NavController
import androidx.navigation.NavGraph
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.createGraph
import com.mog_rn.client.presentation.screens.auth.LoginScreen
import com.mog_rn.client.utils.RouteConfig

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun navGraph(
    navController: NavController,
    isUserLoggedIn: Boolean
): NavGraph {
    val backStackEntryAsState by navController.currentBackStackEntryAsState()


    val startDestination = if (isUserLoggedIn) {
        RouteConfig.HomeScreen.route
    } else {
        RouteConfig.LoginScreen.route
    }

    val navGraph = remember(navController) {
        navController.createGraph(startDestination = startDestination) {
            // Login
            composable(RouteConfig.LoginScreen.route) {
                LoginScreen(navController)
            }
        }
    }

    return navGraph
}