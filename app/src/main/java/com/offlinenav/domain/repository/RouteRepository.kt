package com.offlinenav.domain.repository

import com.offlinenav.domain.model.Location
import com.offlinenav.domain.model.Route
import com.offlinenav.domain.model.RouteType
import com.offlinenav.domain.model.TravelMode

interface RouteRepository {
    suspend fun calculateRoutes(
        origin: Location,
        destination: Location,
        travelMode: TravelMode = TravelMode.CAR,
        routeType: RouteType = RouteType.FASTEST,
        avoidTolls: Boolean = false,
        avoidHighways: Boolean = false
    ): Result<List<Route>>

    suspend fun isOfflineRoutingAvailable(origin: Location, destination: Location): Boolean
}
