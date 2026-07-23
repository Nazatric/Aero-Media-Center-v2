package com.aeromedia.app.ui.screens.video

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import com.aeromedia.app.data.media.VideoItem
import com.aeromedia.app.data.media.VideoStoreRepository
import com.aeromedia.app.data.settings.SettingsRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class VideoUiState(
    val isLoading: Boolean = true,
    val videos: List<VideoItem> = emptyList(),
    val nowPlaying: VideoItem? = null,
    val isPlaying: Boolean = false,
    val positionMs: Long = 0L,
    val durationMs: Long = 0L,
)

/** Owns a real ExoPlayer for video (separate instance from the music
 *  PlaybackViewModel — different queue, different lifecycle) plus the real
 *  on-device video list from VideoStoreRepository. */
class VideoViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = VideoStoreRepository(application)
    private val settings = SettingsRepository(application)
    val player: ExoPlayer = ExoPlayer.Builder(application).build()

    private val _uiState = MutableStateFlow(VideoUiState())
    val uiState: StateFlow<VideoUiState> = _uiState.asStateFlow()

    private var loaded = false

    init {
        player.addListener(object : Player.Listener {
            override fun onIsPlayingChanged(isPlaying: Boolean) {
                _uiState.value = _uiState.value.copy(isPlaying = isPlaying)
            }
        })
        viewModelScope.launch {
            while (true) {
                _uiState.value = _uiState.value.copy(
                    positionMs = player.currentPosition.coerceAtLeast(0L),
                    durationMs = player.duration.coerceAtLeast(0L),
                )
                delay(500)
            }
        }
    }

    fun loadIfNeeded() {
        if (loaded) return
        loaded = true
        viewModelScope.launch {
            val videos = repository.loadAllVideos(settings.excludedVideoFolders)
            _uiState.value = _uiState.value.copy(isLoading = false, videos = videos)
        }
    }

    fun play(video: VideoItem) {
        player.setMediaItem(MediaItem.fromUri(video.contentUri))
        player.prepare()
        player.play()
        _uiState.value = _uiState.value.copy(nowPlaying = video)
    }

    fun closeNowPlaying() {
        player.stop()
        _uiState.value = _uiState.value.copy(nowPlaying = null)
    }

    fun togglePlayPause() {
        if (player.isPlaying) player.pause() else player.play()
    }

    fun seekTo(positionMs: Long) {
        player.seekTo(positionMs)
    }

    override fun onCleared() {
        player.release()
        super.onCleared()
    }
}
