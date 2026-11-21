package com.offlinenav.presentation.navigation

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
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
import com.offlinenav.domain.model.DistanceUnit
import com.offlinenav.domain.model.NavigationState
import com.offlinenav.domain.model.TurnType
import com.offlinenav.presentation.map.components.MapLibreView

@Composable
fun LiveNavigationScreen(
    routeId: String,
    viewModel: NavigationViewModel = hiltViewModel(),
    onEndNavigation: () -> Unit
) {
    val navState by viewModel.navigationState.collectAsState()
    var isMuted by remember { mutableStateOf(viewModel.isMuted()) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MidnightDarkBackground)
    ) {
        val activeRoute = (navState as? NavigationState.Active)?.route
        val currentLoc = (navState as? NavigationState.Active)?.snappedLocation 
            ?: com.offlinenav.domain.model.Location(28.6139, 77.2090, "Current")
        val currentBearing = (navState as? NavigationState.Active)?.snappedLocation?.bearing ?: 0f

        // Live 3D Driving Navigation Map
        MapLibreView(
            modifier = Modifier.fillMaxSize(),
            centerLocation = currentLoc,
            zoom = 16.5,
            tilt = 55.0,
            bearing = currentBearing,
            activeRoute = activeRoute
        )

        // TOP HUD: Maneuver Banner
        when (val state = navState) {
            is NavigationState.Active -> {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .statusBarsPadding()
                        .padding(16.dp)
                ) {
                    EmbossedCard(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(22.dp),
                        backgroundColor = MidnightDarkSurface,
                        elevation = 10.dp
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // Turn Maneuver Direction Icon
                            Box(
                                modifier = Modifier
                                    .size(56.dp)
                                    .background(CyanPrimary, CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                val turnIcon = when (state.currentInstruction.turnType) {
                                    TurnType.LEFT, TurnType.SHARP_LEFT, TurnType.SLIGHT_LEFT -> Icons.Default.TurnLeft
                                    TurnType.RIGHT, TurnType.SHARP_RIGHT, TurnType.SLIGHT_RIGHT -> Icons.Default.TurnRight
                                    TurnType.UTURN -> Icons.Default.RotateLeft
                                    TurnType.ROUNDABOUT_ENTER, TurnType.ROUNDABOUT_EXIT -> Icons.Default.RotateRight
                                    TurnType.ARRIVE -> Icons.Default.Flag
                                    else -> Icons.Default.Straight
                                }
                                Icon(turnIcon, null, tint = Color.Black, modifier = Modifier.size(32.dp))
                            }

                            Spacer(modifier = Modifier.width(16.dp))

                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = DistanceUnit.KILOMETERS.format(state.distanceToNextTurnMeters),
                                    style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
                                    color = CyanNeon
                                )
                                Text(
                                    text = state.currentInstruction.instruction,
                                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                                    color = MidnightTextPrimary
                                )
                                Text(
                                    text = state.currentRoadName,
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MidnightTextSecondary
                                )
                            }
                        }
                    }
                }
            }
            is NavigationState.Rerouting -> {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .statusBarsPadding()
                        .padding(16.dp)
                        .background(AmberGold, RoundedCornerShape(16.dp))
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text("Recalculating route offline...", color = Color.Black, fontWeight = FontWeight.Bold)
                }
            }
            is NavigationState.Arrived -> {
                AlertDialog(
                    onDismissRequest = {
                        viewModel.stopNavigation()
                        onEndNavigation()
                    },
                    title = { Text("Destination Reached!") },
                    text = { Text("You have arrived at your destination safely.") },
                    confirmButton = {
                        Button(onClick = {
                            viewModel.stopNavigation()
                            onEndNavigation()
                        }) {
                            Text("Finish")
                        }
                    }
                )
            }
            else -> {}
        }

        // Floating Action Buttons (Mute, Recenter)
        Column(
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .padding(end = 16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            EmbossedIconButton(
                onClick = { isMuted = viewModel.toggleMute() },
                backgroundColor = MidnightDarkSurface
            ) {
                Icon(
                    imageVector = if (isMuted) Icons.Default.VolumeOff else Icons.Default.VolumeUp,
                    contentDescription = "Mute",
                    tint = if (isMuted) SignalRed else CyanNeon
                )
            }

            EmbossedIconButton(
                onClick = {},
                backgroundColor = MidnightDarkSurface
            ) {
                Icon(Icons.Default.MyLocation, "Recenter", tint = CyanNeon)
            }
        }

        // BOTTOM HUD: Speedometer, Distance, ETA, End Trip
        if (navState is NavigationState.Active) {
            val active = navState as NavigationState.Active
            EmbossedCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.BottomCenter)
                    .navigationBarsPadding()
                    .padding(16.dp),
                shape = RoundedCornerShape(26.dp),
                backgroundColor = MidnightDarkSurface,
                elevation = 12.dp
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    // Speedometer Badge
                    Box(
                        modifier = Modifier
                            .deboss(shape = RoundedCornerShape(16.dp), backgroundColor = MidnightDarkBackground)
                            .padding(horizontal = 14.dp, vertical = 8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = "${active.currentSpeedKmh.toInt()}",
                                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                                color = CyanNeon
                            )
                            Text("KM/H", style = MaterialTheme.typography.labelSmall, color = MidnightTextSecondary)
                        }
                    }

                    // ETA & Remaining Distance
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = DistanceUnit.KILOMETERS.format(active.remainingDistanceMeters),
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = MidnightTextPrimary
                        )
                        Text(
                            text = "${active.remainingDurationSeconds / 60} min remaining",
                            style = MaterialTheme.typography.bodySmall,
                            color = MidnightTextSecondary
                        )
                    }

                    // End Navigation Button
                    Box(
                        modifier = Modifier
                            .size(46.dp)
                            .clip(CircleShape)
                            .background(SignalRed)
                            .clickable {
                                viewModel.stopNavigation()
                                onEndNavigation()
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.Close, contentDescription = "End Trip", tint = Color.White)
                    }
                }
            }
        }
    }
}
