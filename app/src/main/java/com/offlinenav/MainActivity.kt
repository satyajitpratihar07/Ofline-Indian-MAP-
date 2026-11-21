package com.offlinenav

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.view.WindowCompat
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.MultiplePermissionsState
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import com.offlinenav.core.navigation.AppNavigation
import com.offlinenav.core.theme.OfflineNavTheme
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber

/**
 * MainActivity — Single-Activity Architecture.
 *
 * Hosts the Jetpack Compose UI tree and the NavHost.
 * Handles:
 * - Splash screen (via SplashScreen API)
 * - Edge-to-edge display
 * - Runtime permission bootstrapping
 * - Theme application
 */
@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        // Install splash screen BEFORE calling super.onCreate()
        val splashScreen = installSplashScreen()

        super.onCreate(savedInstanceState)

        // Keep splash screen visible while loading initial data
        var keepSplashScreenOn = true
        splashScreen.setKeepOnScreenCondition { keepSplashScreenOn }

        // Enable edge-to-edge display (draws behind status/nav bars)
        enableEdgeToEdge()
        WindowCompat.setDecorFitsSystemWindows(window, false)

        setContent {
            OfflineNavTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                ) {
                    OfflineNavContent(
                        onReady = {
                            keepSplashScreenOn = false
                            Timber.d("App content ready, dismissing splash screen")
                        }
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalPermissionsApi::class)
@Composable
private fun OfflineNavContent(
    onReady: () -> Unit
) {
    // Track whether initial permissions check is done
    var permissionsChecked by remember { mutableStateOf(false) }

    // Location permissions state
    val locationPermissionsState: MultiplePermissionsState = rememberMultiplePermissionsState(
        permissions = listOf(
            android.Manifest.permission.ACCESS_FINE_LOCATION,
            android.Manifest.permission.ACCESS_COARSE_LOCATION,
        )
    )

    LaunchedEffect(Unit) {
        // Signal that app is ready to dismiss splash screen
        onReady()
        permissionsChecked = true
    }

    AppNavigation(
        locationPermissionsState = locationPermissionsState,
        hasLocationPermission = locationPermissionsState.allPermissionsGranted,
    )
}
