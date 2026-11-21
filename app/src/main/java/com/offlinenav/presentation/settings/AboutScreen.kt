package com.offlinenav.presentation.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.offlinenav.core.theme.*

@Composable
fun AboutScreen(
    onNavigateBack: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .statusBarsPadding()
            .navigationBarsPadding()
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            EmbossedIconButton(onClick = onNavigateBack) {
                Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = MaterialTheme.colorScheme.onSurface)
            }
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = "About OfflineNav",
                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onSurface
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        EmbossedCard(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(22.dp)
        ) {
            Column {
                Text("OfflineNav v1.0.0", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold))
                Spacer(modifier = Modifier.height(6.dp))
                Text("Production-grade offline GPS navigation powered by open-source GIS technology.", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)

                Spacer(modifier = Modifier.height(16.dp))

                Text("OpenStreetMap Attribution", fontWeight = FontWeight.Bold, color = CyanPrimary)
                Text(
                    "Map data © OpenStreetMap contributors, licensed under the Open Database License (ODbL).",
                    style = MaterialTheme.typography.bodySmall
                )

                Spacer(modifier = Modifier.height(12.dp))

                Text("Valhalla & MapLibre", fontWeight = FontWeight.Bold, color = CyanPrimary)
                Text(
                    "Valhalla routing engine © Valhalla contributors. Map rendering powered by MapLibre Native SDK.",
                    style = MaterialTheme.typography.bodySmall
                )

                Spacer(modifier = Modifier.height(12.dp))

                Text("Zero Data Collection", fontWeight = FontWeight.Bold, color = SignalGreen)
                Text(
                    "100% of your GPS locations, routes, and searches stay entirely on your device.",
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }
    }
}
