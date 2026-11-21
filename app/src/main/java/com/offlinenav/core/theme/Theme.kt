package com.offlinenav.core.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val DarkColorScheme = darkColorScheme(
    primary = CyanPrimary,
    onPrimary = Color(0xFF001F29),
    primaryContainer = CyanDark,
    onPrimaryContainer = Color(0xFFBBE9FF),
    secondary = AmberGold,
    onSecondary = Color(0xFF432C00),
    secondaryContainer = Color(0xFF614000),
    onSecondaryContainer = Color(0xFFFFDEA4),
    tertiary = SignalGreen,
    onTertiary = Color(0xFF00391A),
    background = MidnightDarkBackground,
    onBackground = MidnightTextPrimary,
    surface = MidnightDarkSurface,
    onSurface = MidnightTextPrimary,
    surfaceVariant = MidnightDarkSurfaceVariant,
    onSurfaceVariant = MidnightTextSecondary,
    outline = Color(0xFF334155),
    error = SignalRed
)

private val LightColorScheme = lightColorScheme(
    primary = CyanDark,
    onPrimary = Color.White,
    primaryContainer = Color(0xFFCBE6FF),
    onPrimaryContainer = Color(0xFF001E30),
    secondary = AmberOrange,
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFFFDCC0),
    onSecondaryContainer = Color(0xFF301400),
    tertiary = SignalGreen,
    onTertiary = Color.White,
    background = GlacierLightBackground,
    onBackground = GlacierTextPrimary,
    surface = GlacierLightSurface,
    onSurface = GlacierTextPrimary,
    surfaceVariant = GlacierLightSurfaceVariant,
    onSurfaceVariant = GlacierTextSecondary,
    outline = Color(0xFFCBD5E1),
    error = SignalRed
)

@Composable
fun OfflineNavTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme
    val view = LocalView.current

    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as? Activity)?.window ?: return@SideEffect
            window.statusBarColor = colorScheme.background.toArgb()
            window.navigationBarColor = colorScheme.background.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
            WindowCompat.getInsetsController(window, view).isAppearanceLightNavigationBars = !darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = OfflineNavTypography,
        content = content
    )
}
