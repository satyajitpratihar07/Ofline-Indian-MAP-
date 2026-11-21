package com.offlinenav.core.navigation

/**
 * Sealed class representing all navigation destinations in the app.
 *
 * Each Screen defines:
 * - [route]: The unique navigation route string
 * - Argument definitions (for screens requiring parameters)
 * - Helper methods to build route strings with arguments
 */
sealed class Screen(val route: String) {

    // ─────────────────────────────────────────
    // Onboarding Flow
    // ─────────────────────────────────────────

    /** Initial splash / loading screen */
    data object Splash : Screen("splash")

    /** Onboarding carousel shown on first launch */
    data object Onboarding : Screen("onboarding")

    // ─────────────────────────────────────────
    // Main Flow
    // ─────────────────────────────────────────

    /** Home dashboard screen */
    data object Home : Screen("home")

    /** Full-screen interactive map */
    data object Map : Screen("map")

    /** Place / address search */
    data object Search : Screen("search")

    /**
     * Route preview shown before starting navigation.
     * Requires [destinationId] argument.
     */
    data object RoutePreview : Screen("route_preview/{destinationId}") {
        const val ARG_DESTINATION_ID = "destinationId"
        fun createRoute(destinationId: String) = "route_preview/$destinationId"
    }

    /**
     * Active turn-by-turn navigation screen.
     * Requires [routeId] argument.
     */
    data object Navigation : Screen("navigation/{routeId}") {
        const val ARG_ROUTE_ID = "routeId"
        fun createRoute(routeId: String) = "navigation/$routeId"
    }

    /** Large speedometer / HUD overlay during navigation */
    data object Speedometer : Screen("speedometer")

    // ─────────────────────────────────────────
    // Downloads
    // ─────────────────────────────────────────

    /** Offline map downloads management */
    data object Downloads : Screen("downloads")

    /**
     * Detail view for a specific map region download.
     * Requires [regionId] argument.
     */
    data object DownloadDetail : Screen("download_detail/{regionId}") {
        const val ARG_REGION_ID = "regionId"
        fun createRoute(regionId: String) = "download_detail/$regionId"
    }

    // ─────────────────────────────────────────
    // User Data
    // ─────────────────────────────────────────

    /** Saved / favourite places */
    data object SavedPlaces : Screen("saved_places")

    /** Navigation history */
    data object History : Screen("history")

    // ─────────────────────────────────────────
    // Settings
    // ─────────────────────────────────────────

    /** Main settings screen */
    data object Settings : Screen("settings")

    /** Voice guidance settings */
    data object VoiceSettings : Screen("voice_settings")

    /** Storage management screen */
    data object Storage : Screen("storage")

    /** About / version info screen */
    data object About : Screen("about")

    // ─────────────────────────────────────────
    // Companion helpers
    // ─────────────────────────────────────────
    companion object {
        /** All top-level (bottom-nav) destinations */
        val topLevelRoutes = listOf(
            Home.route,
            Map.route,
            Search.route,
            Downloads.route,
            Settings.route,
        )

        /** Returns true if the route is a top-level destination */
        fun isTopLevel(route: String?) = topLevelRoutes.contains(route)
    }
}
