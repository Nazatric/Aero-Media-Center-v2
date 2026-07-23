package com.aeromedia.app.ui.screens.video

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.media3.ui.PlayerView
import coil3.ImageLoader
import coil3.compose.AsyncImage
import coil3.video.VideoFrameDecoder
import com.aeromedia.app.data.media.VideoItem
import com.aeromedia.app.ui.components.TransportBar
import com.aeromedia.app.ui.permissions.MediaKind
import com.aeromedia.app.ui.permissions.MediaPermissionGate
import com.aeromedia.app.ui.theme.AeroColors
import com.aeromedia.app.ui.theme.skeuoBrushedMetal
import com.aeromedia.app.ui.theme.skeuoInsetSurface
import com.aeromedia.app.util.DurationFormatter

/**
 * The reference doc's "Video Player" screen doubles as the Library (there's
 * no separate Video Library entry on Home) and deliberately drops the
 * Rip/Burn/Sync-style tabs from the original screenshot — desktop-only
 * actions that "wouldn't work" on a phone, per that same doc.
 */
@Composable
fun VideoPlayerScreen(viewModel: VideoViewModel) {
    MediaPermissionGate(
        kind = MediaKind.VIDEO,
        rationale = "To show your real videos, AeroMedia needs permission to read video from this device.",
    ) {
        viewModel.loadIfNeeded()
        val uiState by viewModel.uiState.collectAsState()

        Column(modifier = Modifier.fillMaxSize().statusBarsPadding()) {
            if (uiState.nowPlaying != null) {
                VideoPlaybackChrome(viewModel = viewModel, video = uiState.nowPlaying!!, isPlaying = uiState.isPlaying, positionMs = uiState.positionMs, durationMs = uiState.durationMs)
            } else {
                VideoLibraryList(videos = uiState.videos, onVideoClick = viewModel::play)
            }
        }
    }
}

@Composable
private fun VideoLibraryList(videos: List<VideoItem>, onVideoClick: (VideoItem) -> Unit) {
    val context = LocalContext.current
    // A real frame from each video, not a static icon — built once per
    // screen instance via Coil3's video-frame decoder.
    val thumbnailLoader = remember {
        ImageLoader.Builder(context)
            .components { add(VideoFrameDecoder.Factory()) }
            .build()
    }

    Column(modifier = Modifier.fillMaxSize()) {
        Text(
            text = "Video Player",
            style = MaterialTheme.typography.headlineMedium,
            color = AeroColors.textPrimary,
            modifier = Modifier.padding(20.dp),
        )
        if (videos.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("No videos found on this device", color = AeroColors.textSecondary)
            }
            return
        }
        LazyColumn(contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)) {
            items(videos, key = { it.id }) { video ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 6.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .clickable { onVideoClick(video) },
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Box(
                        modifier = Modifier
                            .size(width = 88.dp, height = 56.dp)
                            .skeuoInsetSurface(RoundedCornerShape(8.dp)),
                        contentAlignment = Alignment.Center,
                    ) {
                        AsyncImage(
                            model = video.contentUri,
                            imageLoader = thumbnailLoader,
                            contentDescription = video.title,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize(),
                        )
                    }
                    Column(modifier = Modifier.padding(start = 14.dp).weight(1f)) {
                        Text(
                            video.title,
                            color = AeroColors.textPrimary,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                        )
                        Text(
                            DurationFormatter.format(video.durationMs),
                            color = AeroColors.textSecondary,
                            style = MaterialTheme.typography.bodyMedium,
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun VideoPlaybackChrome(
    viewModel: VideoViewModel,
    video: VideoItem,
    isPlaying: Boolean,
    positionMs: Long,
    durationMs: Long,
) {
    Column(modifier = Modifier.fillMaxSize()) {
        // Glossy chrome top bar replacing the original's Now Playing / Library /
        // Rip / Burn / Sync tab row with just what actually functions on a phone.
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .skeuoBrushedMetal()
                .padding(horizontal = 12.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icon(
                imageVector = Icons.Filled.ArrowBack,
                contentDescription = "Back to library",
                tint = AeroColors.surfaceCharcoal,
                modifier = Modifier.clickable { viewModel.closeNowPlaying() },
            )
            Text(
                text = video.title,
                color = AeroColors.surfaceCharcoal,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.padding(start = 14.dp).weight(1f),
            )
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .background(AeroColors.surfaceCharcoal),
            contentAlignment = Alignment.Center,
        ) {
            AndroidView(
                factory = { context ->
                    PlayerView(context).apply {
                        player = viewModel.player
                        useController = false
                    }
                },
                modifier = Modifier.fillMaxSize(),
            )
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .skeuoBrushedMetal()
                .padding(16.dp),
        ) {
            Slider(
                value = if (durationMs > 0) positionMs.toFloat() / durationMs else 0f,
                onValueChange = { fraction -> viewModel.seekTo((fraction * durationMs).toLong()) },
                colors = SliderDefaults.colors(
                    thumbColor = AeroColors.accentBlue,
                    activeTrackColor = AeroColors.accentBlue,
                    inactiveTrackColor = AeroColors.chromeDeepShadow,
                ),
            )
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text(DurationFormatter.format(positionMs), color = AeroColors.surfaceCharcoal, style = MaterialTheme.typography.labelSmall)
                Text(DurationFormatter.format(durationMs), color = AeroColors.surfaceCharcoal, style = MaterialTheme.typography.labelSmall)
            }
            Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                TransportBar(
                    isPlaying = isPlaying,
                    onPlayPause = viewModel::togglePlayPause,
                    onSkipNext = { },
                    onSkipPrevious = { viewModel.seekTo(0L) },
                    modifier = Modifier.padding(top = 8.dp),
                )
            }
        }
    }
}
