package com.aeromedia.app.ui.screens.music

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.aeromedia.app.data.media.Album
import com.aeromedia.app.data.media.MusicStoreRepository
import com.aeromedia.app.data.media.Song
import com.aeromedia.app.data.settings.SettingsRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class MusicUiState(
    val isLoading: Boolean = true,
    val songs: List<Song> = emptyList(),
    val albums: List<Album> = emptyList(),
)

/** Loads the real on-device music library once (on first permission grant)
 *  via MusicStoreRepository — every Song/Album here is real MediaStore data.
 *  Respects whatever folders are excluded in Settings. */
class MusicViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = MusicStoreRepository(application)
    private val settings = SettingsRepository(application)

    private val _uiState = MutableStateFlow(MusicUiState())
    val uiState: StateFlow<MusicUiState> = _uiState.asStateFlow()

    private var loaded = false

    fun loadIfNeeded(forceReload: Boolean = false) {
        if (loaded && !forceReload) return
        loaded = true
        viewModelScope.launch {
            val songs = repository.loadAllSongs(settings.excludedMusicFolders)
            val albums = repository.loadAlbums()
            _uiState.value = MusicUiState(isLoading = false, songs = songs, albums = albums)
        }
    }

    suspend fun songsForAlbum(albumId: Long): List<Song> = repository.loadSongsForAlbum(albumId)
}
