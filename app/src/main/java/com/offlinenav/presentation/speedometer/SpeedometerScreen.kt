package com.offlinenav.presentation.speedometer

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.offlinenav.core.theme.*
import com.offlinenav.domain.model.DistanceUnit
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun SpeedometerScreen(
    viewModel: SpeedometerViewModel = hiltViewModel(),
    onNavigateBack: () -> Unit
) {
    val state by viewModel.uiState.collectAsState()
    val speed = state.tripStats.currentSpeedKmh
    val speedDisplay = state.speedUnit.convertFromKmh(speed)

    val animatedSpeed by animateFloatAsState(
        targetValue = speedDisplay.coerceIn(0f, 220f),
        animationSpec = tween(300),
        label = "needle"
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MidnightDarkBackground)
            .statusBarsPadding()
            .navigationBarsPadding()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Top Bar
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            EmbossedIconButton(
                onClick = onNavigateBack,
                backgroundColor = MidnightDarkSurface
            ) {
                Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = MidnightTextPrimary)
            }

            Text(
                text = "GPS Speedometer",
                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                color = MidnightTextPrimary
            )

            EmbossedIconButton(
                onClick = { viewModel.resetTrip() },
                backgroundColor = MidnightDarkSurface
            ) {
                Icon(Icons.Default.Refresh, contentDescription = "Reset", tint = AmberGold)
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Circular Gauge Canvas
        Box(
            modifier = Modifier
                .size(260.dp),
            contentAlignment = Alignment.Center
        ) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                val strokeWidth = 18.dp.toPx()
                val radius = (size.minDimension - strokeWidth) / 2
                val centerOffset = Offset(size.width / 2, size.height / 2)

                // Background Track Arc
                drawArc(
                    color = Color(0xFF1E293B),
                    startAngle = 135f,
                    sweepAngle = 270f,
                    useCenter = false,
                    style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
                )

                // Active Gradient Arc
                val sweep = (animatedSpeed / 220f) * 270f
                drawArc(
                    color = if (state.isOverSpeedLimit) SignalRed else CyanNeon,
                    startAngle = 135f,
                    sweepAngle = sweep,
                    useCenter = false,
                    style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
                )

                // Needle Line
                val angleRad = Math.toRadians((135.0 + sweep))
                val needleLength = radius * 0.75f
                val needleEnd = Offset(
                    (centerOffset.x + needleLength * cos(angleRad)).toFloat(),
                    (centerOffset.y + needleLength * sin(angleRad)).toFloat()
                )
                drawLine(
                    color = Color.White,
                    start = centerOffset,
                    end = needleEnd,
                    strokeWidth = 4.dp.toPx(),
                    cap = StrokeCap.Round
                )
            }

            // Central Digital Speed Readout
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "${speedDisplay.toInt()}",
                    style = MaterialTheme.typography.displayMedium.copy(fontWeight = FontWeight.Bold),
                    color = if (state.isOverSpeedLimit) SignalRed else MidnightTextPrimary
                )
                Text(
                    text = state.speedUnit.symbol,
                    style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
                    color = CyanNeon
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Unit Switcher (KM/H ↔ MPH)
        EmbossedButton(
            onClick = { viewModel.toggleSpeedUnit() },
            backgroundColor = MidnightDarkSurface,
            modifier = Modifier.wrapContentWidth()
        ) {
            Text(
                text = "Switch to ${if (state.speedUnit == com.offlinenav.domain.model.SpeedUnit.KM_H) "MPH" else "KM/H"}",
                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                color = CyanNeon
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Trip Stats Grid
        val stats = state.tripStats
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                StatCard(
                    modifier = Modifier.weight(1f),
                    title = "Average Speed",
                    value = "${state.speedUnit.convertFromKmh(stats.averageSpeedKmh).toInt()} ${state.speedUnit.symbol}"
                )
                StatCard(
                    modifier = Modifier.weight(1f),
                    title = "Max Speed",
                    value = "${state.speedUnit.convertFromKmh(stats.maxSpeedKmh).toInt()} ${state.speedUnit.symbol}"
                )
            }
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                StatCard(
                    modifier = Modifier.weight(1f),
                    title = "Trip Distance",
                    value = DistanceUnit.KILOMETERS.format(stats.totalDistanceMeters)
                )
                StatCard(
                    modifier = Modifier.weight(1f),
                    title = "Trip Duration",
                    value = "${stats.totalDurationSeconds / 60} min"
                )
            }
        }
    }
}

@Composable
private fun StatCard(modifier: Modifier = Modifier, title: String, value: String) {
    EmbossedCard(
        modifier = modifier,
        shape = RoundedCornerShape(18.dp),
        backgroundColor = MidnightDarkSurface,
        elevation = 6.dp
    ) {
        Column {
            Text(text = title, style = MaterialTheme.typography.labelSmall, color = MidnightTextSecondary)
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = value, style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold), color = MidnightTextPrimary)
        }
    }
}
