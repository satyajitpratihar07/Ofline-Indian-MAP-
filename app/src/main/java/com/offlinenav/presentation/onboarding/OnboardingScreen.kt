package com.offlinenav.presentation.onboarding

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CloudOff
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.offlinenav.core.theme.CyanPrimary
import com.offlinenav.core.theme.EmbossedButton
import com.offlinenav.core.theme.EmbossedCard

data class OnboardingPage(
    val title: String,
    val description: String,
    val icon: ImageVector
)

@Composable
fun OnboardingScreen(onGetStarted: () -> Unit) {
    var currentPage by remember { mutableIntStateOf(0) }

    val pages = listOf(
        OnboardingPage(
            "True Offline Freedom",
            "Download countries or cities once. Search, route, and navigate with zero cellular data or Wi-Fi.",
            Icons.Default.CloudOff
        ),
        OnboardingPage(
            "Global Offline Maps",
            "High-performance OpenStreetMap vector tiles with smooth 2D and 3D architectural perspectives.",
            Icons.Default.Download
        ),
        OnboardingPage(
            "Turn-by-Turn GPS & HUD",
            "Voice-guided navigation, live GPS speedometer, lane guidance, and automatic offline rerouting.",
            Icons.Default.Speed
        )
    )

    val page = pages[currentPage]

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(24.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = 90.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            EmbossedCard(
                modifier = Modifier
                    .size(160.dp),
                shape = CircleShape,
                elevation = 10.dp
            ) {
                Icon(
                    imageVector = page.icon,
                    contentDescription = null,
                    tint = CyanPrimary,
                    modifier = Modifier
                        .size(72.dp)
                        .align(Alignment.Center)
                )
            }

            Spacer(modifier = Modifier.height(48.dp))

            Text(
                text = page.title,
                style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onBackground,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = page.description,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
                lineHeight = 24.sp,
                modifier = Modifier.padding(horizontal = 16.dp)
            )

            Spacer(modifier = Modifier.height(36.dp))

            // Indicator Pills
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                pages.indices.forEach { index ->
                    Box(
                        modifier = Modifier
                            .height(8.dp)
                            .width(if (index == currentPage) 24.dp else 8.dp)
                            .background(
                                color = if (index == currentPage) CyanPrimary else MaterialTheme.colorScheme.outline.copy(alpha = 0.4f),
                                shape = CircleShape
                            )
                    )
                }
            }
        }

        // Bottom Action Button
        EmbossedButton(
            onClick = {
                if (currentPage < pages.size - 1) {
                    currentPage++
                } else {
                    onGetStarted()
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
        ) {
            Text(
                text = if (currentPage == pages.size - 1) "Start Navigating" else "Continue",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onPrimary
            )
        }
    }
}
