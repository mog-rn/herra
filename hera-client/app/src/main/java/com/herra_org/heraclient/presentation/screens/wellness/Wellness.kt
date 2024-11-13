package com.herra_org.heraclient.presentation.screens.wellness

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Message
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun Wellness(navController: NavController) {
    var showEmergencyModal by remember { mutableStateOf(false) }
    var isTimerActive by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()
    val scrollState = rememberScrollState()

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .verticalScroll(scrollState)
                .padding(bottom = 80.dp)
        ) {
            // Top Section
            WellnessHeader()

            // Timer Section
            WellnessTimer(
                isActive = isTimerActive,
                onLongPress = {
                    scope.launch {
                        isTimerActive = true
                        delay(1500) // 1.5 seconds threshold
                        showEmergencyModal = true
                        isTimerActive = false
                    }
                },
                onPress = {
                    isTimerActive = true
                },
                onRelease = {
                    isTimerActive = false
                }
            )

            // Quick Actions
            QuickActions(navController)

            // Trusted Contacts
            TrustedContacts(navController)
        }

        // Emergency Modal
        if (showEmergencyModal) {
            EmergencyAlert(
                onDismiss = { showEmergencyModal = false },
                onConfirm = {
                    // Trigger emergency protocol
                    showEmergencyModal = false
                }
            )
        }
    }
}

@Composable
private fun WellnessHeader() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Text(
            text = "Wellness Center",
            style = MaterialTheme.typography.headlineMedium
        )
        Text(
            text = "Take care of yourself",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun WellnessTimer(
    isActive: Boolean,
    onLongPress: () -> Unit,
    onPress: () -> Unit,
    onRelease: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.secondaryContainer
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Wellness Timer",
                style = MaterialTheme.typography.titleLarge
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Timer Circle
            Box(
                modifier = Modifier
                    .size(120.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.surface)
                    .border(2.dp, MaterialTheme.colorScheme.primary, CircleShape)
                    .pointerInput(Unit) {
                        detectTapGestures(
                            onPress = {
                                onPress()
                                val success = tryAwaitRelease()
                                if (success) {
                                    onRelease()
                                }
                            },
                            onLongPress = { onLongPress() }
                        )
                    },
                contentAlignment = Alignment.Center
            ) {
                AnimatedContent(
                    targetState = isActive,
                    label = "Timer State"
                ) { active ->
                    if (active) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(80.dp),
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = if (active) "Hold..." else "Press",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.primary
                        )
                        if (!active) {
                            Text(
                                text = "and hold",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Hold to start wellness check",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun QuickActions(navController: NavController) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Text(
            text = "Quick Actions",
            style = MaterialTheme.typography.titleLarge
        )

        Spacer(modifier = Modifier.height(16.dp))

        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            modifier = Modifier.height(180.dp),
            userScrollEnabled = false
        ) {
            items(
                listOf(
                    QuickAction("Check In", Icons.Default.CheckCircle) { },
                    QuickAction("Share Location", Icons.Default.LocationOn) { },
                    QuickAction("Call Contact", Icons.Default.Phone) { },
                    QuickAction("Send Message", Icons.Default.Message) { }
                )
            ) { action ->
                QuickActionButton(action)
            }
        }
    }
}

@Composable
private fun TrustedContacts(navController: NavController) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Trusted Contacts",
                    style = MaterialTheme.typography.titleLarge
                )
                TextButton(onClick = { /* Add new contact */ }) {
                    Text("Add New")
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Contact List
            ContactItem(
                name = "Mom",
                phone = "+1 (555) 000-0000",
                onCall = { },
                onMessage = { }
            )
            Spacer(modifier = Modifier.height(8.dp))
            ContactItem(
                name = "Emergency Contact",
                phone = "911",
                onCall = { },
                onMessage = { }
            )
        }
    }
}

@Composable
private fun ContactItem(
    name: String,
    phone: String,
    onCall: () -> Unit,
    onMessage: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text(
                text = name,
                style = MaterialTheme.typography.titleMedium
            )
            Text(
                text = phone,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        Row {
            IconButton(onClick = onCall) {
                Icon(
                    imageVector = Icons.Default.Phone,
                    contentDescription = "Call",
                    tint = MaterialTheme.colorScheme.primary
                )
            }
            IconButton(onClick = onMessage) {
                Icon(
                    imageVector = Icons.Default.Message,
                    contentDescription = "Message",
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}

@Composable
private fun EmergencyAlert(
    onDismiss: () -> Unit,
    onConfirm: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text("Emergency Alert")
        },
        text = {
            Column {
                Text("Your emergency contacts will be notified with:")
                Spacer(modifier = Modifier.height(8.dp))
                Text("• Your current location")
                Text("• Emergency SMS alert")
                Text("• Emergency contact calls")

                Spacer(modifier = Modifier.height(16.dp))

                LinearProgressIndicator(
                    modifier = Modifier.fillMaxWidth(),
                    color = MaterialTheme.colorScheme.error
                )
                Text(
                    text = "Sending in 5 seconds...",
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center
                )
            }
        },
        confirmButton = {
            Button(
                onClick = onConfirm,
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.error
                )
            ) {
                Text("Send Now")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

private data class QuickAction(
    val title: String,
    val icon: ImageVector,
    val onClick: () -> Unit
)

@Composable
private fun QuickActionButton(action: QuickAction) {
    Card(
        modifier = Modifier
            .padding(4.dp)
            .fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.secondaryContainer
        )
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = action.icon,
                contentDescription = action.title,
                modifier = Modifier.size(24.dp),
                tint = MaterialTheme.colorScheme.onSecondaryContainer
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = action.title,
                style = MaterialTheme.typography.labelMedium,
                textAlign = TextAlign.Center
            )
        }
    }
}