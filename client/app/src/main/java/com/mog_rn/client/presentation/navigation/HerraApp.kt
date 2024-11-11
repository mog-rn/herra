package com.mog_rn.client.presentation.navigation

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.mog_rn.client.presentation.components.TopBar
import com.mog_rn.client.utils.RouteConfig

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun HerraApp(
    isLoggedIn: Boolean
) {
    val navController = rememberNavController()
    val backStackEntryState by navController.currentBackStackEntryAsState()
    val screenName by remember { mutableStateOf("") }

    val currentScreen = backStackEntryState?.destination?.route ?: RouteConfig.LoginScreen.route

    Scaffold(topBar = {
        if (currentScreen != RouteConfig.LoginScreen.route) {
            TopBar(currentScreen, screenName = screenName, navigateBack = {
                navController.popBackStack()
            }, logout = {
//                authViewModel.logout()
            })
        }
    }, content = { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            NavHost(
                navController = navController, graph = navGraph(
                    navController, isUserLoggedIn = isLoggedIn
                )
            )
        }
    })
}