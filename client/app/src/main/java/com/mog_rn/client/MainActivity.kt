package com.mog_rn.client

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.mog_rn.client.presentation.navigation.HerraApp
import com.mog_rn.client.presentation.theme.ClientTheme

class MainActivity : ComponentActivity() {
    @Inject
    lateinit var preferencesDataStoreRepository: PreferenceDataStoreRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ClientTheme {
                val isLoggedIn by preferencesDataStoreRepository.isLoggedInFlow.collectAsState(
                    initial = null
                )

                HerraApp(isLoggedIn)
            }
        }
    }
}

