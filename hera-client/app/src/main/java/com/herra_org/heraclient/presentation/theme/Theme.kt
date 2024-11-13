package com.herra_org.heraclient.presentation.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val LightColorScheme = lightColorScheme(
    primary = HerraColors.Purple,
    secondary = HerraColors.LightPurple,
    background = HerraColors.Background,
    surface = HerraColors.CardBackground,
    error = HerraColors.Error
)

private val DarkColorScheme = darkColorScheme(
    primary = HerraColors.Purple,
    secondary = HerraColors.LightPurple,
    background = HerraColors.DarkBackground,
    surface = HerraColors.DarkCardBackground,
    error = HerraColors.Error
)

@Composable
fun HerraTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = HerraTypography,
        shapes = HerraShapes,
        content = content
    )
}