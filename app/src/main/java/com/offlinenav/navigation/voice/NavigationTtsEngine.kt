package com.offlinenav.navigation.voice

import android.content.Context
import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import com.offlinenav.domain.model.DistanceUnit
import com.offlinenav.domain.model.RouteInstruction
import dagger.hilt.android.qualifiers.ApplicationContext
import java.util.Locale
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NavigationTtsEngine @Inject constructor(
    @ApplicationContext private val context: Context
) : TextToSpeech.OnInitListener {

    private var tts: TextToSpeech? = null
    private var isInitialized = false
    private var isMuted = false
    private var lastAnnouncedInstruction: String? = null
    private var lastAnnounceTime = 0L

    init {
        tts = TextToSpeech(context, this)
    }

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            tts?.language = Locale.getDefault()
            isInitialized = true
        }
    }

    fun setLanguage(locale: Locale) {
        if (isInitialized) {
            tts?.language = locale
        }
    }

    fun setMuted(muted: Boolean) {
        this.isMuted = muted
        if (muted) stop()
    }

    fun isMuted(): Boolean = isMuted

    fun speak(text: String, priority: Boolean = false) {
        if (!isInitialized || isMuted) return

        val now = System.currentTimeMillis()
        if (!priority && text == lastAnnouncedInstruction && (now - lastAnnounceTime < 10_000L)) {
            return
        }

        lastAnnouncedInstruction = text
        lastAnnounceTime = now

        val queueMode = if (priority) TextToSpeech.QUEUE_FLUSH else TextToSpeech.QUEUE_ADD
        val params = Bundle().apply {
            putFloat(TextToSpeech.Engine.KEY_PARAM_VOLUME, 1.0f)
        }
        tts?.speak(text, queueMode, params, "NAV_ANNOUNCEMENT_${System.currentTimeMillis()}")
    }

    fun announceManeuver(instruction: RouteInstruction, distanceToTurnMeters: Double) {
        if (isMuted) return

        val spokenDistance = when {
            distanceToTurnMeters > 900 -> "in one kilometer"
            distanceToTurnMeters in 400.0..600.0 -> "in 500 meters"
            distanceToTurnMeters in 150.0..250.0 -> "in 200 meters"
            distanceToTurnMeters in 30.0..70.0 -> "in 50 meters"
            distanceToTurnMeters <= 25.0 -> "now"
            else -> return
        }

        val speech = if (spokenDistance == "now") {
            instruction.instruction
        } else {
            "$spokenDistance, ${instruction.instruction}"
        }

        speak(speech)
    }

    fun announceRerouting() {
        speak("Recalculating route, please wait", priority = true)
    }

    fun announceArrival(destinationName: String) {
        speak("You have arrived at $destinationName", priority = true)
    }

    fun stop() {
        tts?.stop()
    }

    fun shutdown() {
        tts?.stop()
        tts?.shutdown()
        tts = null
        isInitialized = false
    }
}
