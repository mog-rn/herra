package com.herra_org.heraclient.presentation.screens.home.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.herra_org.heraclient.presentation.components.HerraCard

@Composable
fun RecommendationsCard(
    recommendations: List<RecommendationItem>
) {
    HerraCard(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "Today's Recommendations",
                style = MaterialTheme.typography.titleLarge
            )
            Spacer(modifier = Modifier.height(16.dp))

            recommendations.forEach { recommendation ->
                RecommendationItem(
                    icon = recommendation.icon,
                    title = recommendation.title,
                    description = recommendation.description
                )
                if (recommendation != recommendations.last()) {
                    Spacer(modifier = Modifier.height(12.dp))
                }
            }
        }
    }
}

data class RecommendationItem(
    val icon: ImageVector,
    val title: String,
    val description: String
)