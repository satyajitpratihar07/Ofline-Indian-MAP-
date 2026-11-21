package com.offlinenav.core.navigation

import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.MultiplePermissionsState
import com.offlinenav.presentation.downloads.MapDownloadDetailScreen
import com.offlinenav.presentation.downloads.OfflineMapManagerScreen
import com.offlinenav.presentation.history.TravelHistoryScreen
import com.offlinenav.presentation.history.TripDetailScreen
import com.offlinenav.presentation.home.HomeScreen
import com.offlinenav.presentation.map.MapScreen
import com.offlinenav.presentation.navigation.LiveNavigationScreen
import com.offlinenav.presentation.onboarding.OnboardingScreen
import com.offlinenav.presentation.route.RoutePreviewScreen
import com.offlinenav.presentation.saved.SavedPlacesScreen
import com.offlinenav.presentation.search.SearchResultsScreen
import com.offlinenav.presentation.search.SearchScreen
import com.offlinenav.presentation.settings.AboutScreen
import com.offlinenav.presentation.settings.SettingsScreen
import com.offlinenav.presentation.settings.VoiceSettingsScreen
import com.offlinenav.presentation.speedometer.SpeedometerScreen
import com.offlinenav.presentation.splash.SplashScreen

private const val TRANSITION_DURATION_MS = 280

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun AppNavigation(
    navController: NavHostController = rememberNavController(),
    locationPermissionsState: MultiplePermissionsState,
    hasLocationPermission: Boolean,
) {
    NavHost(
        navController = navController,
        startDestination = Screen.Splash.route,
        enterTransition = {
            slideInHorizontally(initialOffsetX = { it }, animationSpec = tween(TRANSITION_DURATION_MS)) +
                    fadeIn(animationSpec = tween(TRANSITION_DURATION_MS))
        },
        exitTransition = {
            slideOutHorizontally(targetOffsetX = { -it }, animationSpec = tween(TRANSITION_DURATION_MS)) +
                    fadeOut(animationSpec = tween(TRANSITION_DURATION_MS))
        },
        popEnterTransition = {
            slideInHorizontally(initialOffsetX = { -it }, animationSpec = tween(TRANSITION_DURATION_MS)) +
                    fadeIn(animationSpec = tween(TRANSITION_DURATION_MS))
        },
        popExitTransition = {
            slideOutHorizontally(targetOffsetX = { it }, animationSpec = tween(TRANSITION_DURATION_MS)) +
                    fadeOut(animationSpec = tween(TRANSITION_DURATION_MS))
        }
    ) {
        // 1. Splash Screen
        composable(Screen.Splash.route) {
            SplashScreen(
                onNavigateToHome = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Splash.route) { inclusive = true }
                    }
                },
                onNavigateToOnboarding = {
                    navController.navigate(Screen.Onboarding.route) {
                        popUpTo(Screen.Splash.route) { inclusive = true }
                    }
                }
            )
        }

        // 2. Onboarding Screen
        composable(Screen.Onboarding.route) {
            OnboardingScreen(
                onGetStarted = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Onboarding.route) { inclusive = true }
                    }
                }
            )
        }

        // 3. Home Screen
        composable(Screen.Home.route) {
            HomeScreen(
                onNavigateToSearch = { navController.navigate(Screen.Search.route) },
                onNavigateToMap = { navController.navigate(Screen.Map.route) },
                onNavigateToDownloads = { navController.navigate(Screen.Downloads.route) },
                onNavigateToSpeedometer = { navController.navigate(Screen.Speedometer.route) },
                onNavigateToSettings = { navController.navigate(Screen.Settings.route) },
                onNavigateToRoutePreview = { dest ->
                    navController.navigate(Screen.RoutePreview.createRoute(dest))
                }
            )
        }

        // 4. Map Screen
        composable(Screen.Map.route) {
            MapScreen(
                onNavigateToSearch = { navController.navigate(Screen.Search.route) },
                onNavigateBack = { navController.popBackStack() }
            )
        }

        // 5. Search Screen
        composable(Screen.Search.route) {
            SearchScreen(
                onNavigateBack = { navController.popBackStack() },
                onSelectPlace = { result ->
                    navController.navigate(Screen.RoutePreview.createRoute(result.name))
                }
            )
        }

        // 6. Route Preview Screen
        composable(
            route = Screen.RoutePreview.route,
            arguments = listOf(navArgument(Screen.RoutePreview.ARG_DESTINATION_ID) { type = NavType.StringType })
        ) { backStackEntry ->
            val destId = backStackEntry.arguments?.getString(Screen.RoutePreview.ARG_DESTINATION_ID) ?: "Destination"
            RoutePreviewScreen(
                destinationId = destId,
                onNavigateBack = { navController.popBackStack() },
                onStartNavigation = { route ->
                    navController.navigate(Screen.Navigation.createRoute(route.id))
                }
            )
        }

        // 7. Live Navigation Screen
        composable(
            route = Screen.Navigation.route,
            arguments = listOf(navArgument(Screen.Navigation.ARG_ROUTE_ID) { type = NavType.StringType })
        ) { backStackEntry ->
            val routeId = backStackEntry.arguments?.getString(Screen.Navigation.ARG_ROUTE_ID) ?: ""
            LiveNavigationScreen(
                routeId = routeId,
                onEndNavigation = {
                    navController.popBackStack(Screen.Home.route, inclusive = false)
                }
            )
        }

        // 8. Speedometer Screen
        composable(Screen.Speedometer.route) {
            SpeedometerScreen(onNavigateBack = { navController.popBackStack() })
        }

        // 9. Downloads Management Screen
        composable(Screen.Downloads.route) {
            OfflineMapManagerScreen(
                onNavigateBack = { navController.popBackStack() },
                onNavigateToDetail = { regionId ->
                    navController.navigate(Screen.DownloadDetail.createRoute(regionId))
                }
            )
        }

        // 10. Download Detail Screen
        composable(
            route = Screen.DownloadDetail.route,
            arguments = listOf(navArgument(Screen.DownloadDetail.ARG_REGION_ID) { type = NavType.StringType })
        ) { backStackEntry ->
            val regionId = backStackEntry.arguments?.getString(Screen.DownloadDetail.ARG_REGION_ID) ?: ""
            MapDownloadDetailScreen(
                regionId = regionId,
                onNavigateBack = { navController.popBackStack() }
            )
        }

        // 11. Saved Places Screen
        composable(Screen.SavedPlaces.route) {
            SavedPlacesScreen(
                onNavigateBack = { navController.popBackStack() },
                onNavigateToRoute = { destName ->
                    navController.navigate(Screen.RoutePreview.createRoute(destName))
                }
            )
        }

        // 12. Travel History Screen
        composable(Screen.History.route) {
            TravelHistoryScreen(
                onNavigateBack = { navController.popBackStack() },
                onNavigateToDetail = { tripId ->
                    navController.navigate("trip_detail/$tripId")
                }
            )
        }

        // 13. Trip Detail Screen
        composable(
            route = "trip_detail/{tripId}",
            arguments = listOf(navArgument("tripId") { type = NavType.LongType })
        ) { backStackEntry ->
            val tripId = backStackEntry.arguments?.getLong("tripId") ?: 0L
            TripDetailScreen(
                tripId = tripId,
                onNavigateBack = { navController.popBackStack() }
            )
        }

        // 14. Settings Screen
        composable(Screen.Settings.route) {
            SettingsScreen(
                onNavigateBack = { navController.popBackStack() },
                onNavigateToVoiceSettings = { navController.navigate(Screen.VoiceSettings.route) },
                onNavigateToAbout = { navController.navigate(Screen.About.route) }
            )
        }

        // 15. Voice Settings Screen
        composable(Screen.VoiceSettings.route) {
            VoiceSettingsScreen(onNavigateBack = { navController.popBackStack() })
        }

        // 16. About Screen
        composable(Screen.About.route) {
            AboutScreen(onNavigateBack = { navController.popBackStack() })
        }
    }
}
