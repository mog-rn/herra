package com.herra_org.heraclient.presentation.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import com.google.accompanist.systemuicontroller.rememberSystemUiController

@Composable
fun SetStatusBarColor(
    color: Color = Color.White,
    darkIcons: Boolean = true
) {
    val systemUiController = rememberSystemUiController()

    // Change the color of the status bar
    systemUiController.setStatusBarColor(
        color = color,
        darkIcons = darkIcons
    )
}