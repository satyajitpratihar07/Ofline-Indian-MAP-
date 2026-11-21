package com.offlinenav.data.routing

import com.offlinenav.domain.model.Location
import com.offlinenav.domain.model.RouteType
import com.offlinenav.domain.model.TravelMode
import org.json.JSONArray
import org.json.JSONObject

object ValhallaRequestBuilder {

    fun buildRouteJson(
        origin: Location,
        destination: Location,
        travelMode: TravelMode,
        routeType: RouteType,
        avoidTolls: Boolean,
        avoidHighways: Boolean
    ): String {
        val root = JSONObject()

        val locations = JSONArray().apply {
            put(JSONObject().apply {
                put("lat", origin.latitude)
                put("lon", origin.longitude)
                put("type", "break")
            })
            put(JSONObject().apply {
                put("lat", destination.latitude)
                put("lon", destination.longitude)
                put("type", "break")
            })
        }
        root.put("locations", locations)

        val costing = when (travelMode) {
            TravelMode.CAR -> "auto"
            TravelMode.MOTORCYCLE -> "motorcycle"
            TravelMode.SCOOTER -> "motor_scooter"
            TravelMode.BICYCLE -> "bicycle"
            TravelMode.WALKING -> "pedestrian"
        }
        root.put("costing", costing)

        val costingOptions = JSONObject().apply {
            val modeOpts = JSONObject().apply {
                if (routeType == RouteType.SHORTEST) {
                    put("shortest", true)
                }
                if (avoidTolls) {
                    put("toll_booth_penalty", 1000.0)
                    put("use_tolls", 0.0)
                }
                if (avoidHighways) {
                    put("use_highways", 0.0)
                }
            }
            put(costing, modeOpts)
        }
        root.put("costing_options", costingOptions)

        val directionsOpts = JSONObject().apply {
            put("units", "kilometers")
            put("language", "en-US")
        }
        root.put("directions_options", directionsOpts)

        return root.toString()
    }
}
