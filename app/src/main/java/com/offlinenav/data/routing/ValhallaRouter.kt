package com.offlinenav.data.routing

import com.offlinenav.domain.model.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ValhallaRouter @Inject constructor(
    private val httpClient: OkHttpClient
) {
    private val localValhallaUrl = "http://127.0.0.1:8002/route"

    suspend fun requestRoute(
        origin: Location,
        destination: Location,
        travelMode: TravelMode,
        routeType: RouteType,
        avoidTolls: Boolean,
        avoidHighways: Boolean
    ): Result<Route> = withContext(Dispatchers.IO) {
        try {
            val requestBodyJson = ValhallaRequestBuilder.buildRouteJson(
                origin, destination, travelMode, routeType, avoidTolls, avoidHighways
            )

            val request = Request.Builder()
                .url(localValhallaUrl)
                .post(requestBodyJson.toRequestBody("application/json".toMediaType()))
                .build()

            val response = httpClient.newCall(request).execute()
            if (!response.isSuccessful) {
                return@withContext Result.failure(Exception("Valhalla local router error: ${response.code}"))
            }

            val body = response.body?.string() ?: return@withContext Result.failure(Exception("Empty body"))
            val route = parseValhallaResponse(body, origin, destination, travelMode, routeType)
            Result.success(route)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private fun parseValhallaResponse(
        jsonString: String,
        origin: Location,
        destination: Location,
        travelMode: TravelMode,
        routeType: RouteType
    ): Route {
        val root = JSONObject(jsonString)
        val trip = root.getJSONObject("trip")
        val summary = trip.getJSONObject("summary")
        val distanceKm = summary.getDouble("length")
        val timeSec = summary.getLong("time")

        val legs = trip.getJSONArray("legs")
        val firstLeg = legs.getJSONObject(0)
        val shape = firstLeg.getString("shape")
        val points = PolylineDecoder.decode(shape, precision = 6)

        val maneuvers = firstLeg.getJSONArray("maneuvers")
        val instructions = mutableListOf<RouteInstruction>()

        for (i in 0 until maneuvers.length()) {
            val m = maneuvers.getJSONObject(i)
            val typeInt = m.optInt("type", 0)
            val text = m.getString("instruction")
            val lengthKm = m.optDouble("length", 0.0)
            val mTime = m.optLong("time", 0L)
            val streetNames = m.optJSONArray("street_names")
            val roadName = if (streetNames != null && streetNames.length() > 0) streetNames.getString(0) else ""

            val turnType = when (typeInt) {
                1, 2, 3 -> TurnType.DEPART
                4, 5 -> TurnType.SLIGHT_RIGHT
                6 -> TurnType.RIGHT
                7 -> TurnType.SHARP_RIGHT
                8 -> TurnType.UTURN
                9, 10 -> TurnType.SHARP_LEFT
                11 -> TurnType.LEFT
                12 -> TurnType.SLIGHT_LEFT
                13, 14 -> TurnType.STRAIGHT
                24 -> TurnType.ROUNDABOUT_ENTER
                26 -> TurnType.ROUNDABOUT_EXIT
                36 -> TurnType.ARRIVE
                else -> TurnType.STRAIGHT
            }

            val shapeIndex = m.optInt("begin_shape_index", 0).coerceIn(0, points.size - 1)
            val stepLoc = points[shapeIndex]

            instructions.add(
                RouteInstruction(
                    stepIndex = i,
                    instruction = text,
                    distanceMeters = lengthKm * 1000.0,
                    durationSeconds = mTime,
                    turnType = turnType,
                    roadName = roadName,
                    latitude = stepLoc.latitude,
                    longitude = stepLoc.longitude,
                    bearing = stepLoc.bearing
                )
            )
        }

        return Route(
            id = UUID.randomUUID().toString(),
            origin = origin,
            destination = destination,
            points = points,
            distanceMeters = distanceKm * 1000.0,
            durationSeconds = timeSec,
            instructions = instructions,
            travelMode = travelMode,
            routeType = routeType,
            polylineEncoded = shape
        )
    }
}
