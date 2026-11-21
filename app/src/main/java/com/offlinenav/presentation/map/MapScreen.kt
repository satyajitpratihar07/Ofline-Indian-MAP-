package com.offlinenav.presentation.map

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.offlinenav.core.theme.*
import com.offlinenav.presentation.map.components.MapLibreView

@Composable
fun MapScreen(
    viewModel: MapViewModel = hiltViewModel(),
    onNavigateToSearch: () -> Unit,
    onNavigateBack: () -> Unit
) {
    val state by viewModel.uiState.collectAsState()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Real Live MapLibre View
        MapLibreView(
            modifier = Modifier.fillMaxSize(),
            centerLocation = state.userLocation,
            zoom = state.zoomLevel,
            tilt = state.mapPitch,
            bearing = state.compassBearing,
            activeRoute = state.activeRoute
        )

        // Top Floating Bar with Back Button & Search
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            EmbossedIconButton(onClick = onNavigateBack) {
                Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = MaterialTheme.colorScheme.onSurface)
            }

            Spacer(modifier = Modifier.width(12.dp))

            EmbossedCard(
                modifier = Modifier
                    .weight(1f),
                shape = RoundedCornerShape(16.dp),
                elevation = 6.dp
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Lat: ${String.format("%.4f", state.userLocation.latitude)}, Lng: ${String.format("%.4f", state.userLocation.longitude)}",
                        style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.SemiBold),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Box(
                        modifier = Modifier
                            .background(SignalGreen.copy(alpha = 0.2f), RoundedCornerShape(8.dp))
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text("GPS FIX", color = SignalGreen, style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold))
                    }
                }
            }
        }

        // Right Side Embossed Controls: 2D/3D, Compass, Recenter
        Column(
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .padding(end = 16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // 2D / 3D Toggle
            EmbossedIconButton(
                onClick = { viewModel.toggle3DMode() }
            ) {
                Text(
                    text = if (state.is3DMode) "3D" else "2D",
                    style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
                    color = if (state.is3DMode) CyanNeon else MaterialTheme.colorScheme.onSurface
                )
            }

            // Compass
            EmbossedIconButton(onClick = {}) {
                Icon(
                    imageVector = Icons.Default.Explore,
                    contentDescription = "Compass",
                    tint = AmberGold
                )
            }

            // Recenter
            EmbossedIconButton(
                onClick = { viewModel.recenterOnUser() }
            ) {
                Icon(
                    imageVector = Icons.Default.MyLocation,
                    contentDescription = "My Location",
                    tint = CyanPrimary
                )
            }
        }

        // Bottom Banner
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
                .navigationBarsPadding()
                .padding(16.dp)
        ) {
            EmbossedButton(
                onClick = onNavigateToSearch,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Directions, null, tint = MaterialTheme.colorScheme.onPrimary)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        "Search Destination",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                }
            }
        }
    }
}
