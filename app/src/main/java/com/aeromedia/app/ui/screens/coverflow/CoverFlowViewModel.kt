package com.aeromedia.app.ui.screens.coverflow

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.aeromedia.app.data.media.MusicStoreRepository
import com.aeromedia.app.data.media.Song
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/** One card in the carousel — either an album or a track, normalized to the
 *  same shape so CoverFlowScreen doesn't need to know which mode it's in. */
data class CoverFlowCard(
    val id: Long,
    val title: String,
    val subtitle: String,
    val artworkUri: android.net.Uri,
)

enum class CoverFlowMode { ALBUMS, TRACKS }

data class CoverFlowUiState(
    val mode: CoverFlowMode = CoverFlowMode.ALBUMS,
    val albumCards: List<CoverFlowCard> = emptyList(),
    val trackCards: List<CoverFlowCard> = emptyList(),
) {
    val cards: List<CoverFlowCard> get() = if (mode == CoverFlowMode.ALBUMS) albumCards else trackCards
}

/** Real album art / real track metadata, reusing MusicStoreRepository —
 *  Cover Flow is a different *view* over the same real library, not a
 *  separate data source. */
class CoverFlowViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = MusicStoreRepository(application)
    private val _uiState = MutableStateFlow(CoverFlowUiState())
    val uiState: StateFlow<CoverFlowUiState> = _uiState.asStateFlow()

    private var loaded = false

    fun loadIfNeeded() {
        if (loaded) return
        loaded = true
        viewModelScope.launch {
            val albums = repository.loadAlbums()
            val songs = repository.loadAllSongs()
            _uiState.value = _uiState.value.copy(
                albumCards = albums.map { CoverFlowCard(it.id, it.title, it.artist, it.artworkUri) },
                trackCards = songs.map { song: Song ->
                    CoverFlowCard(song.id, song.title, song.artist, MusicStoreRepository.albumArtUriFor(song.albumId))
                },
            )
        }
    }

    fun toggleMode() {
        _uiState.value = _uiState.value.copy(
            mode = if (_uiState.value.mode == CoverFlowMode.ALBUMS) CoverFlowMode.TRACKS else CoverFlowMode.ALBUMS,
        )
    }
}
