package com.aeromedia.app.ui.screens.settings

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.aeromedia.app.data.media.MusicStoreRepository
import com.aeromedia.app.data.media.PictureStoreRepository
import com.aeromedia.app.data.media.VideoStoreRepository
import com.aeromedia.app.data.settings.SettingsRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class SettingsUiState(
    val soundsEnabled: Boolean = true,
    val musicFolders: List<String> = emptyList(),
    val videoFolders: List<String> = emptyList(),
    val pictureFolders: List<String> = emptyList(),
    val excludedMusicFolders: Set<String> = emptySet(),
    val excludedVideoFolders: Set<String> = emptySet(),
    val excludedPictureFolders: Set<String> = emptySet(),
)

/**
 * Settings backed by real data in two ways: the folder lists come from an
 * actual scan of the device's music/video/picture libraries (not a
 * hardcoded list), and every toggle here is read back by the Music/Video/
 * Pictures ViewModels the next time they load.
 *
 * The reference doc's ".amp file" theme-editor idea (re-skinning every
 * icon/text/UI element) is a real, sizeable feature on its own — deliberately
 * scoped out of Phase 1 as a named roadmap item rather than half-built here.
 */
class SettingsViewModel(application: Application) : AndroidViewModel(application) {

    private val settings = SettingsRepository(application)
    private val musicRepo = MusicStoreRepository(application)
    private val videoRepo = VideoStoreRepository(application)
    private val pictureRepo = PictureStoreRepository(application)

    private val _uiState = MutableStateFlow(
        SettingsUiState(
            soundsEnabled = settings.soundsEnabled,
            excludedMusicFolders = settings.excludedMusicFolders,
            excludedVideoFolders = settings.excludedVideoFolders,
            excludedPictureFolders = settings.excludedPictureFolders,
        ),
    )
    val uiState: StateFlow<SettingsUiState> = _uiState.asStateFlow()

    private var loaded = false

    fun loadIfNeeded() {
        if (loaded) return
        loaded = true
        viewModelScope.launch {
            val music = musicRepo.loadDistinctFolders()
            val video = videoRepo.loadDistinctFolders()
            val pictures = pictureRepo.loadDistinctFolders()
            _uiState.value = _uiState.value.copy(
                musicFolders = music,
                videoFolders = video,
                pictureFolders = pictures,
            )
        }
    }

    fun setSoundsEnabled(enabled: Boolean) {
        settings.soundsEnabled = enabled
        _uiState.value = _uiState.value.copy(soundsEnabled = enabled)
    }

    fun toggleMusicFolder(folder: String) {
        val updated = settings.toggleFolder(settings.excludedMusicFolders, folder)
        settings.excludedMusicFolders = updated
        _uiState.value = _uiState.value.copy(excludedMusicFolders = updated)
    }

    fun toggleVideoFolder(folder: String) {
        val updated = settings.toggleFolder(settings.excludedVideoFolders, folder)
        settings.excludedVideoFolders = updated
        _uiState.value = _uiState.value.copy(excludedVideoFolders = updated)
    }

    fun togglePictureFolder(folder: String) {
        val updated = settings.toggleFolder(settings.excludedPictureFolders, folder)
        settings.excludedPictureFolders = updated
        _uiState.value = _uiState.value.copy(excludedPictureFolders = updated)
    }
}
