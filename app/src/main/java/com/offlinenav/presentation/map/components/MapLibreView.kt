package com.offlinenav.presentation.map.components

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import com.offlinenav.R
import com.offlinenav.domain.model.Location
import com.offlinenav.domain.model.Route
import org.maplibre.android.annotations.MarkerOptions
import org.maplibre.android.annotations.PolylineOptions
import org.maplibre.android.camera.CameraPosition
import org.maplibre.android.camera.CameraUpdateFactory
import org.maplibre.android.geometry.LatLng
import org.maplibre.android.maps.MapLibreMap
import org.maplibre.android.maps.MapView
import org.maplibre.android.maps.Style

@Composable
fun MapLibreView(
    modifier: Modifier = Modifier,
    centerLocation: Location = Location(20.5937, 78.9629, "India"), // Center of India
    zoom: Double = 5.0,
    tilt: Double = 0.0,
    bearing: Float = 0f,
    activeRoute: Route? = null,
    isDarkMode: Boolean = isSystemInDarkTheme(),
    onMapClick: (Location) -> Unit = {}
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    val mapView = remember {
        MapView(context).apply {
            onCreate(null)
        }
    }

    var mapInstance by remember { mutableStateOf<MapLibreMap?>(null) }
    var currentStyleLoaded by remember { mutableStateOf(false) }

    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_START -> mapView.onStart()
                Lifecycle.Event.ON_RESUME -> mapView.onResume()
                Lifecycle.Event.ON_PAUSE -> mapView.onPause()
                Lifecycle.Event.ON_STOP -> mapView.onStop()
                Lifecycle.Event.ON_DESTROY -> mapView.onDestroy()
                else -> {}
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
            try {
                mapView.onDestroy()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    // Initialize MapLibre Map and set OSM / Carto style
    LaunchedEffect(mapView) {
        mapView.getMapAsync { map ->
            mapInstance = map
            val styleRes = if (isDarkMode) R.raw.map_style_dark else R.raw.map_style_light
            val styleJson = context.resources.openRawResource(styleRes).bufferedReader().use { it.readText() }

            map.setStyle(Style.Builder().fromJson(styleJson)) { style ->
                currentStyleLoaded = true

                // Move camera to India / center location
                val initialPos = CameraPosition.Builder()
                    .target(LatLng(centerLocation.latitude, centerLocation.longitude))
                    .zoom(zoom)
                    .tilt(tilt)
                    .bearing(bearing.toDouble())
                    .build()
                map.cameraPosition = initialPos

                map.addOnMapClickListener { point ->
                    onMapClick(Location(point.latitude, point.longitude))
                    true
                }
            }
        }
    }

    // React to camera changes
    LaunchedEffect(centerLocation, zoom, tilt, bearing) {
        val map = mapInstance ?: return@LaunchedEffect
        if (currentStyleLoaded) {
            val cameraPos = CameraPosition.Builder()
                .target(LatLng(centerLocation.latitude, centerLocation.longitude))
                .zoom(zoom)
                .tilt(tilt)
                .bearing(bearing.toDouble())
                .build()
            map.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPos), 800)
        }
    }

    // React to route rendering
    LaunchedEffect(activeRoute, mapInstance, currentStyleLoaded) {
        val map = mapInstance ?: return@LaunchedEffect
        if (!currentStyleLoaded) return@LaunchedEffect

        map.clear()

        val route = activeRoute
        if (route != null && route.points.isNotEmpty()) {
            val latLngs = route.points.map { LatLng(it.latitude, it.longitude) }

            // Primary Route Line in Blue
            map.addPolyline(
                PolylineOptions()
                    .addAll(latLngs)
                    .color(android.graphics.Color.parseColor("#0077FE"))
                    .width(6f)
            )

            // Start & Destination Markers
            map.addMarker(
                MarkerOptions()
                    .position(LatLng(route.origin.latitude, route.origin.longitude))
                    .title("Origin: " + route.origin.name)
            )
            map.addMarker(
                MarkerOptions()
                    .position(LatLng(route.destination.latitude, route.destination.longitude))
                    .title("Destination: " + route.destination.name)
            )
        }
    }

    AndroidView(
        factory = { mapView },
        modifier = modifier
    )
}
