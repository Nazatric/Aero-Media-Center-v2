package com.aeromedia.app.ui.playback

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import com.aeromedia.app.data.media.Song
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * Real Media3/ExoPlayer playback — not a fake progress bar. Owns one
 * ExoPlayer instance for the process lifetime; queues real Song content
 * URIs (see MusicStoreRepository) and exposes position/duration as state
 * for the Now Playing screen's scrubber.
 *
 * Phase 1 scope: plays while the app is in the foreground, via a plain
 * ExoPlayer. Background/lock-screen playback needs a MediaSessionService —
 * see the note in AndroidManifest.xml; that's a clean, self-contained
 * Phase 2 addition, not started here so nothing ships half-wired.
 */
class PlaybackViewModel(application: Application) : AndroidViewModel(application) {

    private val player = ExoPlayer.Builder(application).build()

    private val _state = MutableStateFlow(PlaybackUiState())
    val state: StateFlow<PlaybackUiState> = _state.asStateFlow()

    init {
        player.addListener(object : Player.Listener {
            override fun onIsPlayingChanged(isPlaying: Boolean) {
                _state.value = _state.value.copy(isPlaying = isPlaying)
            }

            override fun onMediaItemTransition(mediaItem: MediaItem?, reason: Int) {
                val index = player.currentMediaItemIndex
                val song = _state.value.queue.getOrNull(index)
                _state.value = _state.value.copy(currentIndex = index, currentSong = song)
            }

            override fun onPlaybackStateChanged(playbackState: Int) {
                _state.value = _state.value.copy(
                    durationMs = player.duration.coerceAtLeast(0L),
                )
            }
        })

        // Cheap position polling instead of a Handler — good enough for a
        // once-per-second scrubber update, no extra threading machinery.
        viewModelScope.launch {
            while (true) {
                _state.value = _state.value.copy(
                    positionMs = player.currentPosition.coerceAtLeast(0L),
                    durationMs = player.duration.coerceAtLeast(0L),
                )
                delay(500)
            }
        }
    }

    /** Replace the queue with [songs] and start playing at [startIndex]. Used
     *  both by "play this track" (a one-song queue starting at 0) and
     *  "play album" (the whole album's songs, starting at the tapped one). */
    fun playQueue(songs: List<Song>, startIndex: Int) {
        if (songs.isEmpty()) return
        player.setMediaItems(songs.map { MediaItem.fromUri(it.contentUri) })
        player.seekTo(startIndex, 0L)
        player.prepare()
        player.play()
        _state.value = _state.value.copy(
            queue = songs,
            currentIndex = startIndex,
            currentSong = songs[startIndex],
        )
    }

    fun togglePlayPause() {
        if (player.isPlaying) player.pause() else player.play()
    }

    fun skipToNext() {
        if (player.hasNextMediaItem()) player.seekToNext()
    }

    fun skipToPrevious() {
        if (player.currentPosition > 3000L) {
            player.seekTo(0L)
        } else if (player.hasPreviousMediaItem()) {
            player.seekToPrevious()
        } else {
            player.seekTo(0L)
        }
    }

    fun seekTo(positionMs: Long) {
        player.seekTo(positionMs)
    }

    /** Pause and rewind to the start — a real "stop", not just pause, for the
     *  mini-player's stop button. */
    fun stop() {
        player.pause()
        player.seekTo(0L)
    }

    fun toggleShuffle() {
        player.shuffleModeEnabled = !player.shuffleModeEnabled
        _state.value = _state.value.copy(shuffleEnabled = player.shuffleModeEnabled)
    }

    fun toggleRepeat() {
        player.repeatMode = if (player.repeatMode == Player.REPEAT_MODE_OFF) Player.REPEAT_MODE_ALL else Player.REPEAT_MODE_OFF
        _state.value = _state.value.copy(repeatEnabled = player.repeatMode != Player.REPEAT_MODE_OFF)
    }

    override fun onCleared() {
        player.release()
        super.onCleared()
    }
}

data class PlaybackUiState(
    val queue: List<Song> = emptyList(),
    val currentIndex: Int = -1,
    val currentSong: Song? = null,
    val isPlaying: Boolean = false,
    val positionMs: Long = 0L,
    val durationMs: Long = 0L,
    val shuffleEnabled: Boolean = false,
    val repeatEnabled: Boolean = false,
)
