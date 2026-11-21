package com.offlinenav.presentation.route

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.offlinenav.core.theme.*
import com.offlinenav.domain.model.Route
import com.offlinenav.domain.model.TravelMode
import com.offlinenav.presentation.map.components.MapLibreView

@Composable
fun RoutePreviewScreen(
    destinationId: String,
    viewModel: RouteViewModel = hiltViewModel(),
    onNavigateBack: () -> Unit,
    onStartNavigation: (Route) -> Unit
) {
    val state by viewModel.uiState.collectAsState()

    LaunchedEffect(destinationId) {
        viewModel.loadRoutes(
            com.offlinenav.domain.model.Location(28.7041, 77.1025, destinationId)
        )
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Real Live Map Preview
        MapLibreView(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = 300.dp),
            centerLocation = state.origin,
            zoom = 11.0,
            activeRoute = state.selectedRoute
        )

        // Top Navigation Bar
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
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(16.dp),
                elevation = 4.dp
            ) {
                Column {
                    Text(
                        text = "To: ${state.destination.name}",
                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "From: ${state.origin.name}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }

        // Bottom Routing Control Sheet
        EmbossedCard(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
                .navigationBarsPadding()
                .padding(16.dp),
            shape = RoundedCornerShape(26.dp),
            elevation = 12.dp
        ) {
            Column {
                // Travel Mode Tabs
                LazyRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    items(TravelMode.values()) { mode ->
                        val isSelected = state.selectedTravelMode == mode
                        val icon = when (mode) {
                            TravelMode.CAR -> Icons.Default.DirectionsCar
                            TravelMode.MOTORCYCLE -> Icons.Default.TwoWheeler
                            TravelMode.SCOOTER -> Icons.Default.Moped
                            TravelMode.BICYCLE -> Icons.Default.DirectionsBike
                            TravelMode.WALKING -> Icons.Default.DirectionsWalk
                        }
                        Box(
                            modifier = Modifier
                                .emboss(shape = CircleShape, elevation = if (isSelected) 2.dp else 4.dp)
                                .clip(CircleShape)
                                .background(if (isSelected) CyanPrimary else MaterialTheme.colorScheme.surface)
                                .clickable { viewModel.setTravelMode(mode) }
                                .padding(12.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = icon,
                                contentDescription = mode.title,
                                tint = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurface,
                                modifier = Modifier.size(22.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Route Cards (Fastest vs Shortest vs Alternative)
                state.routes.forEachIndexed { index, route ->
                    val isSelected = state.selectedRouteIndex == index
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                            .deboss(
                                shape = RoundedCornerShape(16.dp),
                                backgroundColor = if (isSelected) CyanPrimary.copy(alpha = 0.15f) else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
                            )
                            .clickable { viewModel.selectRoute(index) }
                            .padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = if (index == 0) "Fastest Route" else "Alternative ${index}",
                                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                                    color = if (isSelected) CyanPrimary else MaterialTheme.colorScheme.onSurface
                                )
                                if (index == 0) {
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Box(
                                        modifier = Modifier
                                            .background(SignalGreen.copy(alpha = 0.2f), RoundedCornerShape(6.dp))
                                            .padding(horizontal = 6.dp, vertical = 2.dp)
                                    ) {
                                        Text("BEST", color = SignalGreen, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                    }
                                }
                            }
                            Text(
                                text = "ETA: ${route.etaFormatted} • ${route.distanceFormatted}",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        Text(
                            text = route.durationFormatted,
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = if (isSelected) CyanPrimary else MaterialTheme.colorScheme.onSurface
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Start Navigation Button
                val selected = state.selectedRoute
                EmbossedButton(
                    onClick = {
                        selected?.let { onStartNavigation(it) }
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Navigation, null, tint = MaterialTheme.colorScheme.onPrimary)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            "Start Offline Navigation",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.onPrimary
                        )
                    }
                }
            }
        }
    }
}
