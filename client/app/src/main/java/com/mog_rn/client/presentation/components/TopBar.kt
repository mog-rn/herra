package com.mog_rn.client.presentation.components

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopBar(
    currentScreen: String,
    screenName: String,
    navigateBack: () -> Unit,
    logout: () -> Unit = {}
) {
    CenterAlignedTopAppBar(
        windowInsets = WindowInsets(left = 20.dp, right = 20.dp, top = 5.dp, bottom = 5.dp),
        colors = TopAppBarDefaults.topAppBarColors(
//            containerColor = TertiaryBlue,
        ),
        title = {
            Text(
                text = screenName,
                color = Color(0XFF405202),
                fontSize = 20.sp,
                fontWeight = FontWeight.SemiBold,
                textAlign = TextAlign.Center
            )
        },
        actions = {}
    )
}