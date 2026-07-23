package com.aeromedia.app.util

import android.content.Context
import android.media.AudioManager
import com.aeromedia.app.data.settings.SettingsRepository

/**
 * Real tap/navigation sound effects — not a silent toggle. Uses Android's
 * built-in system sound-effect samples via `AudioManager.playSoundEffect`
 * (the same mechanism every Android keyboard/dialer click sound uses), so
 * there's a real, immediate sound with no audio asset to source or bundle.
 * Respects the "Sounds" toggle in Settings (`SettingsRepository.soundsEnabled`).
 */
class SoundEffects(context: Context) {
    private val appContext = context.applicationContext
    private val audioManager = appContext.getSystemService(Context.AUDIO_SERVICE) as AudioManager
    private val settings = SettingsRepository(appContext)

    init {
        audioManager.loadSoundEffects()
    }

    /** A menu-row / generic tap. */
    fun tap() = play(AudioManager.FX_KEY_CLICK)

    /** Entering a screen / opening something. */
    fun navigateForward() = play(AudioManager.FX_KEYPRESS_STANDARD)

    /** Leaving a screen / closing something. */
    fun navigateBack() = play(AudioManager.FX_KEYPRESS_DELETE)

    /** Play/pause toggles, favoriting, and other small state flips. */
    fun toggle() = play(AudioManager.FX_KEYPRESS_SPACEBAR)

    private fun play(effectType: Int) {
        if (!settings.soundsEnabled) return
        // The (effectType, volume) overload plays at an explicit volume
        // regardless of the *system-wide* touch-sounds setting — this app's
        // own Settings toggle is the one source of truth for whether it makes
        // noise, not a global OS switch the person may not have touched.
        audioManager.playSoundEffect(effectType, 1.0f)
    }
}
