package com.aeromedia.app.ui.screens.music

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.aeromedia.app.data.media.Album
import com.aeromedia.app.data.media.MusicStoreRepository
import com.aeromedia.app.data.media.Song
import com.aeromedia.app.ui.components.TransportBar
import com.aeromedia.app.ui.permissions.MediaKind
import com.aeromedia.app.ui.permissions.MediaPermissionGate
import com.aeromedia.app.ui.playback.PlaybackViewModel
import com.aeromedia.app.ui.theme.AeroColors
import com.aeromedia.app.ui.theme.skeuoInsetSurface
import com.aeromedia.app.util.DurationFormatter
import kotlinx.coroutines.launch

/**
 * Three-pane swipe navigation: Tracks (0) <-> Now Playing (1) <-> Albums (2),
 * looping back to Tracks past Albums — per the reference doc: "swiping left
 * in music" reaches Now Playing, swiping left again reaches Albums, and
 * swiping left again returns to Tracks.
 */
@Composable
fun MusicPagerScreen(
    musicViewModel: MusicViewModel,
    playbackViewModel: PlaybackViewModel,
) {
    MediaPermissionGate(
        kind = MediaKind.AUDIO,
        rationale = "To show your real tracks and albums, AeroMedia needs permission to read music from this device.",
    ) {
        musicViewModel.loadIfNeeded()
        val uiState by musicViewModel.uiState.collectAsState()
        val playbackState by playbackViewModel.state.collectAsState()
        // Arriving with something already queued (e.g. tapping the mini-player's
        // expand button) should land on Now Playing, not back on Tracks.
        val startPage = remember { if (playbackViewModel.state.value.currentSong != null) 1 else 0 }
        val pagerState = rememberPagerState(initialPage = startPage, pageCount = { 3 })
        val scope = rememberCoroutineScope()

        Column(modifier = Modifier.fillMaxSize().statusBarsPadding()) {
            HorizontalPager(state = pagerState, modifier = Modifier.weight(1f)) { page ->
                when (page) {
                    0 -> TracksPane(
                        songs = uiState.songs,
                        onSongClick = { index ->
                            playbackViewModel.playQueue(uiState.songs, index)
                            scope.launch { pagerState.animateScrollToPage(1) }
                        },
                    )
                    1 -> NowPlayingPane(playbackViewModel = playbackViewModel, state = playbackState)
                    else -> AlbumsPane(
                        albums = uiState.albums,
                        onAlbumClick = { album ->
                            scope.launch {
                                val songs = musicViewModel.songsForAlbum(album.id)
                                if (songs.isNotEmpty()) {
                                    playbackViewModel.playQueue(songs, 0)
                                    pagerState.animateScrollToPage(1)
                                }
                            }
                        },
                    )
                }
            }
            PageDots(count = 3, currentPage = pagerState.currentPage)
        }
    }
}

@Composable
private fun PageDots(count: Int, currentPage: Int) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 10.dp),
        contentAlignment = Alignment.Center,
    ) {
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            repeat(count) { index ->
                Box(
                    modifier = Modifier
                        .size(7.dp)
                        .clip(CircleShape)
                        .background(if (index == currentPage) AeroColors.skyDuskOrange else AeroColors.divider),
                )
            }
        }
    }
}

@Composable
private fun TracksPane(songs: List<Song>, onSongClick: (Int) -> Unit) {
    Column(modifier = Modifier.fillMaxSize()) {
        Text(
            text = "Tracks",
            style = MaterialTheme.typography.headlineMedium,
            color = AeroColors.textPrimary,
            modifier = Modifier.padding(20.dp),
        )
        if (songs.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("No tracks found on this device", color = AeroColors.textSecondary)
            }
            return
        }
        LazyColumn(contentPadding = PaddingValues(bottom = 24.dp)) {
            itemsIndexed(songs, key = { _, item -> item.id }) { index, song ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(10.dp))
                        .background(AeroColors.surfaceCharcoalLight.copy(alpha = 0.4f))
                        .padding(horizontal = 20.dp, vertical = 4.dp)
                        .clickableSong { onSongClick(index) },
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    AsyncImage(
                        model = MusicStoreRepository.albumArtUriFor(song.albumId),
                        contentDescription = null,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .size(44.dp)
                            .clip(RoundedCornerShape(6.dp))
                            .skeuoInsetSurface(RoundedCornerShape(6.dp)),
                    )
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .padding(start = 14.dp, top = 10.dp, bottom = 10.dp),
                    ) {
                        Text(
                            song.title,
                            color = AeroColors.textPrimary,
                            fontWeight = FontWeight.Medium,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                        )
                        Text(
                            "${song.artist} • ${song.album}",
                            color = AeroColors.textSecondary,
                            style = MaterialTheme.typography.bodyMedium,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                        )
                    }
                    Text(
                        DurationFormatter.format(song.durationMs),
                        color = AeroColors.textSecondary,
                        style = MaterialTheme.typography.labelSmall,
                    )
                }
                HorizontalDivider(color = AeroColors.divider, thickness = 0.5.dp)
            }
        }
    }
}

private fun Modifier.clickableSong(onClick: () -> Unit): Modifier =
    this.clickable(onClick = onClick)

@Composable
private fun NowPlayingPane(
    playbackViewModel: PlaybackViewModel,
    state: com.aeromedia.app.ui.playback.PlaybackUiState,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        val song = state.currentSong
        Box(
            modifier = Modifier
                .fillMaxWidth(0.72f)
                .aspectRatio(1f)
                .skeuoInsetSurface(RoundedCornerShape(16.dp)),
            contentAlignment = Alignment.Center,
        ) {
            if (song != null) {
                AsyncImage(
                    model = MusicStoreRepository.albumArtUriFor(song.albumId),
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize(0.86f).clip(RoundedCornerShape(10.dp)),
                )
            } else {
                Text("Nothing playing yet", color = AeroColors.textSecondary)
            }
        }

        Text(
            text = song?.title ?: "Pick a track from Tracks or Albums",
            style = MaterialTheme.typography.headlineMedium,
            color = AeroColors.textPrimary,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.padding(top = 24.dp),
        )
        if (song != null) {
            Text(
                text = "${song.artist} — ${song.album}",
                style = MaterialTheme.typography.bodyMedium,
                color = AeroColors.textSecondary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }

        Slider(
            value = if (state.durationMs > 0) state.positionMs.toFloat() / state.durationMs else 0f,
            onValueChange = { fraction -> playbackViewModel.seekTo((fraction * state.durationMs).toLong()) },
            colors = SliderDefaults.colors(
                thumbColor = AeroColors.accentGreen,
                activeTrackColor = AeroColors.accentGreen,
                inactiveTrackColor = AeroColors.divider,
            ),
            modifier = Modifier.padding(top = 20.dp),
        )
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Text(DurationFormatter.format(state.positionMs), color = AeroColors.textSecondary, style = MaterialTheme.typography.labelSmall)
            Text(DurationFormatter.format(state.durationMs), color = AeroColors.textSecondary, style = MaterialTheme.typography.labelSmall)
        }

        TransportBar(
            isPlaying = state.isPlaying,
            onPlayPause = playbackViewModel::togglePlayPause,
            onSkipNext = playbackViewModel::skipToNext,
            onSkipPrevious = playbackViewModel::skipToPrevious,
            modifier = Modifier.padding(top = 20.dp),
        )
    }
}

@Composable
private fun AlbumsPane(albums: List<Album>, onAlbumClick: (Album) -> Unit) {
    Column(modifier = Modifier.fillMaxSize()) {
        Text(
            text = "Albums",
            style = MaterialTheme.typography.headlineMedium,
            color = AeroColors.textPrimary,
            modifier = Modifier.padding(20.dp),
        )
        if (albums.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("No albums found on this device", color = AeroColors.textSecondary)
            }
            return
        }
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(14.dp),
            verticalArrangement = Arrangement.spacedBy(18.dp),
        ) {
            items(albums, key = { it.id }) { album ->
                Column(
                    modifier = Modifier.clickableSong { onAlbumClick(album) },
                ) {
                    AsyncImage(
                        model = album.artworkUri,
                        contentDescription = null,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .fillMaxWidth()
                            .aspectRatio(1f)
                            .skeuoInsetSurface(RoundedCornerShape(10.dp)),
                    )
                    Text(
                        album.title,
                        color = AeroColors.textPrimary,
                        fontWeight = FontWeight.Medium,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.padding(top = 8.dp),
                    )
                    Text(
                        album.artist,
                        color = AeroColors.textSecondary,
                        style = MaterialTheme.typography.bodyMedium,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                }
            }
        }
    }
}
