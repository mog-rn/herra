package com.herra_org.heraclient.presentation.navigation

import androidx.compose.foundation.background
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Color.Companion.White
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.NavGraph.Companion.findStartDestination
import com.herra_org.heraclient.presentation.theme.HerraColors.Background
import com.herra_org.heraclient.presentation.theme.HerraColors.LightPurple
import com.herra_org.heraclient.presentation.theme.HerraColors.Purple
import com.herra_org.heraclient.utils.BottomNavConfig

@Composable
fun BottomNavBar(
    navController: NavController,
    items: List<BottomNavConfig>
) {
    var selectedItem by remember { mutableIntStateOf(0) }

    NavigationBar(
        containerColor = Purple,
        contentColor = White,
        modifier = Modifier
            .background(Background)
            .clip(
                RoundedCornerShape(
                    topStart = 20.dp,
                    topEnd = 20.dp,
                    bottomEnd = 0.dp,
                    bottomStart = 0.dp
                )
            )
    ) {
        items.forEachIndexed { index, item ->
            val isSelected = selectedItem == index
            NavigationBarItem(
                icon = {
                    Icon(
                        item.icon,
                        contentDescription = item.title,
                    )
                },
                label = {
                    Text(item.title)
                },
                selected = isSelected,
                onClick = {
                    selectedItem = index
                    navController.navigate(item.route) {
                        popUpTo(navController.graph.findStartDestination().id) {
                            saveState = true
                        }
                        restoreState = true
                        launchSingleTop = true
                    }
                },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = White,
                    selectedTextColor = LightPurple,
//                    indicatorColor = DarkSlateGray,
                    unselectedIconColor = LightPurple,
                    unselectedTextColor = LightPurple,
                    disabledIconColor = Color.Gray,
                    disabledTextColor = Color.Gray
                )
            )
        }
    }
}