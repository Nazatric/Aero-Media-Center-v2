package com.aeromedia.app.ui.components

import android.content.Context
import android.media.AudioManager
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.OpenInFull
import androidx.compose.material.icons.filled.Repeat
import androidx.compose.material.icons.filled.Shuffle
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.aeromedia.app.ui.playback.PlaybackViewModel
import com.aeromedia.app.ui.theme.AeroColors
import com.aeromedia.app.ui.theme.skeuoBrushedMetal
import com.aeromedia.app.util.DurationFormatter
import com.aeromedia.app.util.LocalSoundEffects

/**
 * The persistent "Aero Media Player" bar from the reference screenshot —
 * shown at the bottom of every screen (Home included), not just Music, since
 * that's exactly what the reference shows: playback controls visible while
 * browsing the menu. Real controls throughout: shuffle/repeat toggle actual
 * ExoPlayer modes, the slider moves actual device media volume.
 */
@Composable
fun MiniPlayerBar(playbackViewModel: PlaybackViewModel, onExpand: () -> Unit) {
    val state by playbackViewModel.state.collectAsState()
    val soundEffects = LocalSoundEffects.current
    val context = LocalContext.current
    val audioManager = remember { context.getSystemService(Context.AUDIO_SERVICE) as AudioManager }
    var volume by remember {
        mutableFloatStateOf(
            audioManager.getStreamVolume(AudioManager.STREAM_MUSIC)
                .toFloat() / audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC).coerceAtLeast(1),
        )
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .skeuoBrushedMetal()
            .padding(horizontal = 10.dp, vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column(modifier = Modifier.width(74.dp)) {
            Text(
                text = "AeroMedia",
                color = AeroColors.surfaceCharcoal,
                style = MaterialTheme.typography.labelSmall,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            Text(
                text = DurationFormatter.format(state.positionMs),
                color = AeroColors.surfaceCharcoal,
                style = MaterialTheme.typography.labelSmall,
            )
        }

        MiniIconToggle(
            icon = Icons.Filled.Shuffle,
            active = state.shuffleEnabled,
            onClick = { soundEffects.toggle(); playbackViewModel.toggleShuffle() },
        )
        MiniIconToggle(
            icon = Icons.Filled.Repeat,
            active = state.repeatEnabled,
            onClick = { soundEffects.toggle(); playbackViewModel.toggleRepeat() },
        )
        Icon(
            imageVector = Icons.Filled.Stop,
            contentDescription = "Stop",
            tint = AeroColors.surfaceCharcoal,
            modifier = Modifier
                .size(20.dp)
                .clickable { soundEffects.tap(); playbackViewModel.stop() },
        )

        Spacer(modifier = Modifier.width(4.dp))
        TransportBar(
            isPlaying = state.isPlaying,
            onPlayPause = playbackViewModel::togglePlayPause,
            onSkipNext = playbackViewModel::skipToNext,
            onSkipPrevious = playbackViewModel::skipToPrevious,
        )
        Spacer(modifier = Modifier.width(4.dp))

        Icon(Icons.Filled.VolumeUp, contentDescription = "Volume", tint = AeroColors.surfaceCharcoal, modifier = Modifier.size(18.dp))
        Slider(
            value = volume,
            onValueChange = { fraction ->
                volume = fraction
                val max = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC)
                audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, (fraction * max).toInt(), 0)
            },
            colors = SliderDefaults.colors(thumbColor = AeroColors.accentBlue, activeTrackColor = AeroColors.accentBlue),
            modifier = Modifier.width(56.dp),
        )

        Icon(
            imageVector = Icons.Filled.OpenInFull,
            contentDescription = "Expand player",
            tint = AeroColors.surfaceCharcoal,
            modifier = Modifier
                .size(16.dp)
                .clickable { soundEffects.tap(); onExpand() },
        )
    }
}

@Composable
private fun MiniIconToggle(icon: ImageVector, active: Boolean, onClick: () -> Unit) {
    Icon(
        imageVector = icon,
        contentDescription = null,
        tint = if (active) AeroColors.accentBlue else AeroColors.textSecondary,
        modifier = Modifier
            .size(20.dp)
            .padding(horizontal = 3.dp)
            .clickable(onClick = onClick),
    )
}
