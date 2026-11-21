package com.offlinenav.core.theme

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * High-end Embossed (Neumorphic) Visual Modifier.
 * Provides soft dual-lighting:
 * - Top-left light specular reflection
 * - Bottom-right deep ambient drop shadow
 * Gives buttons, cards, and dials a tactile, physical, luxury automotive feel.
 */
@Composable
fun Modifier.emboss(
    shape: Shape = RoundedCornerShape(16.dp),
    elevation: Dp = 6.dp,
    lightColor: Color = if (MaterialTheme.colorScheme.background.red < 0.2f) Color(0x334A6572) else Color(0x80FFFFFF),
    shadowColor: Color = if (MaterialTheme.colorScheme.background.red < 0.2f) Color(0x99000000) else Color(0x26000000),
    isPressed: Boolean = false
): Modifier {
    val currentElevation = if (isPressed) (elevation / 3) else elevation
    return this
        .shadow(
            elevation = currentElevation,
            shape = shape,
            ambientColor = shadowColor,
            spotColor = shadowColor
        )
        .border(
            width = 1.dp,
            brush = Brush.linearGradient(
                colors = if (isPressed) {
                    listOf(shadowColor.copy(alpha = 0.4f), lightColor.copy(alpha = 0.2f))
                } else {
                    listOf(lightColor.copy(alpha = 0.7f), shadowColor.copy(alpha = 0.3f))
                },
                start = Offset(0f, 0f),
                end = Offset(Float.POSITIVE_INFINITY, Float.POSITIVE_INFINITY)
            ),
            shape = shape
        )
}

/**
 * Debossed (Sunken / Inset) style for gauge bezels, text inputs, and active states.
 */
@Composable
fun Modifier.deboss(
    shape: Shape = RoundedCornerShape(14.dp),
    backgroundColor: Color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f)
): Modifier {
    val isDark = MaterialTheme.colorScheme.background.red < 0.2f
    val innerShadow = if (isDark) Color(0x80000000) else Color(0x2B000000)
    val bottomHighlight = if (isDark) Color(0x20FFFFFF) else Color(0x80FFFFFF)

    return this
        .clip(shape)
        .background(backgroundColor)
        .border(
            width = 1.5.dp,
            brush = Brush.verticalGradient(
                listOf(innerShadow, bottomHighlight)
            ),
            shape = shape
        )
}

/**
 * Pre-composed Tactile Embossed Card.
 */
@Composable
fun EmbossedCard(
    modifier: Modifier = Modifier,
    shape: Shape = RoundedCornerShape(18.dp),
    elevation: Dp = 6.dp,
    backgroundColor: Color = MaterialTheme.colorScheme.surface,
    content: @Composable BoxScope.() -> Unit
) {
    Box(
        modifier = modifier
            .emboss(shape = shape, elevation = elevation)
            .clip(shape)
            .background(backgroundColor)
            .padding(16.dp),
        content = content
    )
}

/**
 * Tactile Embossed Button with haptic press compression effect.
 */
@Composable
fun EmbossedButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    shape: Shape = RoundedCornerShape(16.dp),
    backgroundColor: Color = MaterialTheme.colorScheme.primary,
    elevation: Dp = 6.dp,
    content: @Composable BoxScope.() -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.97f else 1.0f,
        label = "embossButtonScale"
    )

    Box(
        modifier = modifier
            .emboss(
                shape = shape,
                elevation = elevation,
                isPressed = isPressed
            )
            .clip(shape)
            .background(
                Brush.linearGradient(
                    colors = if (isPressed) {
                        listOf(backgroundColor.copy(alpha = 0.9f), backgroundColor)
                    } else {
                        listOf(backgroundColor, backgroundColor.copy(alpha = 0.85f))
                    }
                )
            )
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = onClick
            )
            .padding(horizontal = 20.dp, vertical = 14.dp),
        contentAlignment = Alignment.Center,
        content = content
    )
}

/**
 * Circular Embossed FAB for Map Navigation Controls (Recenter, Compass, 2D/3D).
 */
@Composable
fun EmbossedIconButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    backgroundColor: Color = MaterialTheme.colorScheme.surface,
    elevation: Dp = 5.dp,
    content: @Composable BoxScope.() -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    Box(
        modifier = modifier
            .emboss(
                shape = CircleShape,
                elevation = elevation,
                isPressed = isPressed
            )
            .clip(CircleShape)
            .background(backgroundColor)
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = onClick
            )
            .padding(12.dp),
        contentAlignment = Alignment.Center,
        content = content
    )
}
