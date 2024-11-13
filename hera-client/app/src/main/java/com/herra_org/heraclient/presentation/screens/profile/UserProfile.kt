package com.herra_org.heraclient.presentation.screens.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.herra_org.heraclient.utils.NavGraphs
import com.herra_org.heraclient.utils.RouteConfig

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserProfile(navController: NavController) {
    var showLogoutDialog by remember { mutableStateOf(false) }
    val scrollState = rememberScrollState()

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .verticalScroll(scrollState)
        ) {
            // Profile Header
            ProfileHeader(
                onEditProfile = {
                    navController.navigate(RouteConfig.EditProfile.route)
                }
            )

            // Quick Stats
            QuickStats()

            // Settings Sections
            SettingsSection(navController)

            // Logout Button
            LogoutButton(onClick = { showLogoutDialog = true })

            // Bottom Spacing for better scroll experience
            Spacer(modifier = Modifier.height(80.dp))
        }

        // Logout Dialog
        if (showLogoutDialog) {
            LogoutConfirmationDialog(
                onDismiss = { showLogoutDialog = false },
                onConfirm = {
                    navController.navigate(NavGraphs.AUTH) {
                        popUpTo(NavGraphs.MAIN) { inclusive = true }
                    }
                }
            )
        }
    }
}

@Composable
private fun ProfileHeader(
    onEditProfile: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Profile Picture
        Box(
            modifier = Modifier
                .size(120.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.secondaryContainer),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Person,
                contentDescription = "Profile Picture",
                modifier = Modifier.size(48.dp),
                tint = MaterialTheme.colorScheme.onSecondaryContainer
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // User Info
        Text(
            text = "Sarah Johnson",
            style = MaterialTheme.typography.headlineMedium
        )
        Text(
            text = "sarah.j@example.com",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Edit Profile Button
        OutlinedButton(
            onClick = onEditProfile,
            modifier = Modifier.width(200.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Edit,
                contentDescription = null,
                modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text("Edit Profile")
        }
    }
}

@Composable
private fun QuickStats() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        StatItem(
            label = "Cycle Length",
            value = "28 days"
        )
        StatItem(
            label = "Average Phase",
            value = "14 days"
        )
        StatItem(
            label = "Tracked Months",
            value = "6"
        )
    }
}

@Composable
private fun StatItem(
    label: String,
    value: String
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = value,
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.primary
        )
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun SettingsSection(navController: NavController) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        // Account Settings
        SettingsGroup(
            title = "Account",
            items = listOf(
                SettingsItem(
                    title = "Personal Information",
                    icon = Icons.Outlined.Person,
                    onClick = {
                        navController.navigate(RouteConfig.EditProfile.route)
                    }
                ),
                SettingsItem(
                    title = "Emergency Contacts",
                    icon = Icons.Outlined.ContactPhone,
                    onClick = {
                        navController.navigate(RouteConfig.EmergencyContacts.route)
                    }
                ),
                SettingsItem(
                    title = "Privacy",
                    icon = Icons.Outlined.Security,
                    onClick = {
                        navController.navigate(RouteConfig.Privacy.route)
                    }
                )
            )
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Preferences
        SettingsGroup(
            title = "Preferences",
            items = listOf(
                SettingsItem(
                    title = "Notifications",
                    icon = Icons.Outlined.Notifications,
                    onClick = {
                        navController.navigate(RouteConfig.Notifications.route)
                    }
                ),
                SettingsItem(
                    title = "Cycle Settings",
                    icon = Icons.Outlined.CalendarToday,
                    onClick = {
                        navController.navigate(RouteConfig.CycleSetup.route)
                    }
                ),
                SettingsItem(
                    title = "Appearance",
                    icon = Icons.Outlined.Palette,
                    onClick = {
                        navController.navigate(RouteConfig.Settings.route)
                    },
                    trailing = {
                        Text(
                            text = "Light",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                )
            )
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Support
        SettingsGroup(
            title = "Support",
            items = listOf(
                SettingsItem(
                    title = "Help & FAQ",
                    icon = Icons.Outlined.Help,
                    onClick = {
                        navController.navigate(RouteConfig.Help.route)
                    }
                ),
                SettingsItem(
                    title = "About",
                    icon = Icons.Outlined.Info,
                    onClick = { /* Navigate */ }
                )
            )
        )
    }
}

@Composable
private fun SettingsGroup(
    title: String,
    items: List<SettingsItem>
) {
    Column {
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(vertical = 8.dp)
        )
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            )
        ) {
            Column {
                items.forEachIndexed { index, item ->
                    SettingsItemRow(item = item)
                    if (index < items.size - 1) {
                        Divider(
                            modifier = Modifier.padding(horizontal = 16.dp),
                            color = MaterialTheme.colorScheme.outlineVariant
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SettingsItemRow(item: SettingsItem) {
    ListItem(
        headlineContent = {
            Text(item.title)
        },
        leadingContent = {
            Icon(
                imageVector = item.icon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
        },
        trailingContent = {
            item.trailing?.invoke() ?: Icon(
                imageVector = Icons.Default.ChevronRight,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
        },
        modifier = Modifier.clickable(onClick = item.onClick)
    )
}

@Composable
private fun LogoutButton(onClick: () -> Unit) {
    TextButton(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        colors = ButtonDefaults.textButtonColors(
            contentColor = MaterialTheme.colorScheme.error
        )
    ) {
        Icon(
            imageVector = Icons.Default.Logout,
            contentDescription = null,
            modifier = Modifier.size(16.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text("Log Out")
    }
}

@Composable
private fun LogoutConfirmationDialog(
    onDismiss: () -> Unit,
    onConfirm: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text("Log Out")
        },
        text = {
            Text("Are you sure you want to log out?")
        },
        confirmButton = {
            Button(
                onClick = onConfirm,
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.error
                )
            ) {
                Text("Log Out")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

private data class SettingsItem(
    val title: String,
    val icon: ImageVector,
    val onClick: () -> Unit,
    val trailing: (@Composable () -> Unit)? = null
)