package com.herra_org.heraclient

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.herra_org.heraclient.presentation.navigation.HerraApp
import com.herra_org.heraclient.presentation.theme.HerraTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            HerraTheme {
                HerraApp()
            }
        }
    }
}
