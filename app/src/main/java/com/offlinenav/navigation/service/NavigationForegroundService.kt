package com.offlinenav.navigation.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import com.offlinenav.MainActivity
import com.offlinenav.domain.model.DistanceUnit
import com.offlinenav.domain.model.NavigationState
import com.offlinenav.navigation.engine.NavigationEngine
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

@AndroidEntryPoint
class NavigationForegroundService : Service() {

    @Inject lateinit var navigationEngine: NavigationEngine

    private val scope = CoroutineScope(Dispatchers.Main + Job())
    private var stateJob: Job? = null

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        startForeground(NOTIFICATION_ID, buildNotification("Turn-by-turn Navigation", "Initializing route guidance..."))

        stateJob?.cancel()
        stateJob = navigationEngine.navigationState
            .onEach { state ->
                when (state) {
                    is NavigationState.Active -> {
                        val turnText = state.currentInstruction.instruction
                        val distFormatted = DistanceUnit.KILOMETERS.format(state.remainingDistanceMeters)
                        val etaMins = (state.remainingDurationSeconds / 60)
                        val subText = "Remaining: $distFormatted • $etaMins min (${state.currentSpeedKmh.toInt()} km/h)"
                        updateNotification(turnText, subText)
                    }
                    is NavigationState.Rerouting -> {
                        updateNotification("Rerouting...", "Finding best alternative route")
                    }
                    is NavigationState.Arrived -> {
                        updateNotification("Arrived!", "You have reached your destination.")
                        stopForeground(STOP_FOREGROUND_DETACH)
                        stopSelf()
                    }
                    NavigationState.Idle -> {
                        stopForeground(STOP_FOREGROUND_REMOVE)
                        stopSelf()
                    }
                    NavigationState.Calculating -> {}
                }
            }
            .launchIn(scope)

        return START_STICKY
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Turn-by-Turn Navigation",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Shows live turn directions, ETA, and speed during navigation"
                setSound(null, null)
            }
            val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            manager.createNotificationChannel(channel)
        }
    }

    private fun buildNotification(title: String, content: String): Notification {
        val launchIntent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_SINGLE_TOP
        }
        val pendingIntent = PendingIntent.getActivity(
            this, 0, launchIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle(title)
            .setContentText(content)
            .setSmallIcon(android.R.drawable.ic_menu_directions)
            .setContentIntent(pendingIntent)
            .setOngoing(true)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .build()
    }

    private fun updateNotification(title: String, content: String) {
        val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        manager.notify(NOTIFICATION_ID, buildNotification(title, content))
    }

    override fun onDestroy() {
        stateJob?.cancel()
        super.onDestroy()
    }

    companion object {
        const val CHANNEL_ID = "offline_navigation_channel"
        const val NOTIFICATION_ID = 2002

        fun start(context: Context) {
            val intent = Intent(context, NavigationForegroundService::class.java)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                context.startForegroundService(intent)
            } else {
                context.startService(intent)
            }
        }

        fun stop(context: Context) {
            val intent = Intent(context, NavigationForegroundService::class.java)
            context.stopService(intent)
        }
    }
}
