package com.herra_org.heraclient.presentation.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

object HerraButtons {
    @Composable
    fun Primary(
        text: String,
        onClick: () -> Unit,
        modifier: Modifier = Modifier,
        enabled: Boolean = true,
        loading: Boolean = false
    ) {
        Button(
            onClick = onClick,
            modifier = modifier
                .height(56.dp)
                .fillMaxWidth(),
            enabled = enabled && !loading,
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            ),
            shape = MaterialTheme.shapes.medium
        ) {
            if (loading) {
                CircularProgressIndicator(
                    color = MaterialTheme.colorScheme.onPrimary,
                    modifier = Modifier.size(24.dp)
                )
            } else {
                Text(
                    text = text,
                    style = MaterialTheme.typography.labelLarge
                )
            }
        }
    }

    @Composable
    fun Secondary(
        text: String,
        onClick: () -> Unit,
        modifier: Modifier = Modifier
    ) {
        Button(
            onClick = onClick,
            modifier = modifier
                .height(56.dp)
                .fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.secondary,
                contentColor = MaterialTheme.colorScheme.primary
            ),
            shape = MaterialTheme.shapes.medium
        ) {
            Text(
                text = text,
                style = MaterialTheme.typography.labelLarge
            )
        }
    }
}
