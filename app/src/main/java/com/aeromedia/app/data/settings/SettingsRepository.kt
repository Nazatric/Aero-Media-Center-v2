package com.aeromedia.app.data.settings

import android.content.Context

/**
 * App settings, backed by plain SharedPreferences — small, simple key-value
 * state (folder exclusions, a sounds toggle) that doesn't need Room. Real
 * persistence: survives restarts, actually filters the Music/Video/Pictures
 * repositories (see how each *ViewModel reads these before loading).
 */
class SettingsRepository(context: Context) {
    private val prefs = context.applicationContext.getSharedPreferences("aeromedia_settings", Context.MODE_PRIVATE)

    var soundsEnabled: Boolean
        get() = prefs.getBoolean(KEY_SOUNDS_ENABLED, true)
        set(value) = prefs.edit().putBoolean(KEY_SOUNDS_ENABLED, value).apply()

    var excludedMusicFolders: Set<String>
        get() = prefs.getStringSet(KEY_EXCLUDED_MUSIC, emptySet()) ?: emptySet()
        set(value) = prefs.edit().putStringSet(KEY_EXCLUDED_MUSIC, value).apply()

    var excludedVideoFolders: Set<String>
        get() = prefs.getStringSet(KEY_EXCLUDED_VIDEO, emptySet()) ?: emptySet()
        set(value) = prefs.edit().putStringSet(KEY_EXCLUDED_VIDEO, value).apply()

    var excludedPictureFolders: Set<String>
        get() = prefs.getStringSet(KEY_EXCLUDED_PICTURES, emptySet()) ?: emptySet()
        set(value) = prefs.edit().putStringSet(KEY_EXCLUDED_PICTURES, value).apply()

    fun toggleFolder(current: Set<String>, folder: String): Set<String> =
        if (folder in current) current - folder else current + folder

    private companion object {
        const val KEY_SOUNDS_ENABLED = "sounds_enabled"
        const val KEY_EXCLUDED_MUSIC = "excluded_music_folders"
        const val KEY_EXCLUDED_VIDEO = "excluded_video_folders"
        const val KEY_EXCLUDED_PICTURES = "excluded_picture_folders"
    }
}
