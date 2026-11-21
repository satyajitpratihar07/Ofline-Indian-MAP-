package com.offlinenav.data.routing

import com.offlinenav.domain.model.Location
import com.offlinenav.domain.model.Route
import com.offlinenav.domain.model.RouteType
import com.offlinenav.domain.model.TravelMode
import com.offlinenav.domain.repository.RouteRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RouteRepositoryImpl @Inject constructor(
    private val valhallaRouter: ValhallaRouter,
    private val offlineGraphRouter: OfflineGraphRouter
) : RouteRepository {

    override suspend fun calculateRoutes(
        origin: Location,
        destination: Location,
        travelMode: TravelMode,
        routeType: RouteType,
        avoidTolls: Boolean,
        avoidHighways: Boolean
    ): Result<List<Route>> {
        // Attempt local Valhalla daemon first
        val valhallaResult = valhallaRouter.requestRoute(
            origin, destination, travelMode, routeType, avoidTolls, avoidHighways
        )

        if (valhallaResult.isSuccess) {
            val primaryRoute = valhallaResult.getOrThrow()
            // Generate alternative route
            val alternative = offlineGraphRouter.calculateRoute(
                origin, destination, travelMode, routeType, isAlternative = true
            )
            return Result.success(listOf(primaryRoute, alternative))
        }

        // Seamless high-speed offline graph router fallback
        val primary = offlineGraphRouter.calculateRoute(
            origin, destination, travelMode, RouteType.FASTEST, isAlternative = false
        )
        val shortest = offlineGraphRouter.calculateRoute(
            origin, destination, travelMode, RouteType.SHORTEST, isAlternative = false
        )
        val alternative = offlineGraphRouter.calculateRoute(
            origin, destination, travelMode, RouteType.FASTEST, isAlternative = true
        )

        val routes = if (routeType == RouteType.SHORTEST) {
            listOf(shortest, primary, alternative)
        } else {
            listOf(primary, shortest, alternative)
        }

        return Result.success(routes)
    }

    override suspend fun isOfflineRoutingAvailable(origin: Location, destination: Location): Boolean {
        return true
    }
}
