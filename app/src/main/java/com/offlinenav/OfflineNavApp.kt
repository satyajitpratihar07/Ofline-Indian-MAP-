package com.offlinenav

import android.app.Application
import androidx.hilt.work.HiltWorkerFactory
import androidx.work.Configuration
import dagger.hilt.android.HiltAndroidApp
import okhttp3.Cache
import okhttp3.OkHttpClient
import org.maplibre.android.MapLibre
import org.maplibre.android.module.http.HttpRequestUtil
import timber.log.Timber
import java.io.File
import java.util.concurrent.TimeUnit
import javax.inject.Inject

/**
 * OfflineNav Application class.
 *
 * Serves as the entry point for the application lifecycle.
 * Initializes MapLibre Native SDK, OkHttpClient with OSM-compliant User-Agent,
 * Hilt for dependency injection, Timber for logging, and WorkManager.
 */
@HiltAndroidApp
class OfflineNavApp : Application(), Configuration.Provider {

    @Inject
    lateinit var workerFactory: HiltWorkerFactory

    override fun onCreate() {
        super.onCreate()

        // 1. MUST initialize MapLibre Native SDK FIRST before any HTTP or MapView operations
        MapLibre.getInstance(this)

        // 2. Configure MapLibre HTTP Client to comply with OpenStreetMap Tile Usage Policy
        try {
            val tileCacheDir = File(cacheDir, "osm_tile_cache").apply { mkdirs() }
            val mapLibreHttpClient = OkHttpClient.Builder()
                .cache(Cache(tileCacheDir, 100L * 1024 * 1024)) // 100 MB disk tile cache
                .connectTimeout(15, TimeUnit.SECONDS)
                .readTimeout(15, TimeUnit.SECONDS)
                .addNetworkInterceptor { chain ->
                    val request = chain.request().newBuilder()
                        .removeHeader("User-Agent")
                        .header("User-Agent", "OfflineNavApp/1.0.0 (Linux; Android; com.offlinenav; contact:support@offlinenav.org)")
                        .header("Accept", "image/webp,image/png,image/*;q=0.8")
                        .header("Referer", "https://offlinenav.org/")
                        .build()
                    chain.proceed(request)
                }
                .build()
            HttpRequestUtil.setOkHttpClient(mapLibreHttpClient)
            Timber.i("MapLibre OkHttpClient successfully configured with OSM-compliant User-Agent")
        } catch (e: Throwable) {
            Timber.e(e, "Failed to configure MapLibre HTTP Client")
        }

        // 3. Initialize Timber logging tree
        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        } else {
            Timber.plant(ReleaseTree())
        }

        Timber.i("OfflineNavApp initialized — version ${BuildConfig.VERSION_NAME}")
    }

    /**
     * Provides WorkManager configuration with Hilt's worker factory,
     * enabling dependency injection in Worker classes.
     */
    override val workManagerConfiguration: Configuration
        get() = Configuration.Builder()
            .setWorkerFactory(workerFactory)
            .setMinimumLoggingLevel(
                if (BuildConfig.DEBUG) android.util.Log.DEBUG
                else android.util.Log.ERROR
            )
            .build()

    /**
     * Production Timber logging tree.
     * Filters verbose/debug logs and can integrate with crash reporting.
     */
    private class ReleaseTree : Timber.Tree() {
        override fun log(priority: Int, tag: String?, message: String, t: Throwable?) {
            // In production, only log warnings and errors
            if (priority < android.util.Log.WARN) return

            android.util.Log.println(priority, tag ?: "OfflineNav", message)
        }
    }
}
