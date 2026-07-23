package com.aeromedia.app.ui.screens.favorites

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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.aeromedia.app.data.media.MusicStoreRepository
import com.aeromedia.app.ui.components.FavoriteHeartButton
import com.aeromedia.app.ui.theme.AeroColors
import com.aeromedia.app.ui.theme.aeroSkyBackground
import com.aeromedia.app.ui.theme.skeuoChromePill
import com.aeromedia.app.ui.theme.skeuoInsetSurface
import com.aeromedia.app.util.DurationFormatter

private enum class FavoritesPage(val label: String) {
    PHOTOS("Photos"),
    TRACKS_ALBUMS("Tracks & Albums"),
    VIDEOS("Videos"),
}

@Composable
fun FavoritesScreen(viewModel: FavoritesViewModel) {
    LaunchedEffect(Unit) { viewModel.refresh() }
    val uiState by viewModel.uiState.collectAsState()
    var page by remember { mutableStateOf(FavoritesPage.TRACKS_ALBUMS) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .aeroSkyBackground()
            .statusBarsPadding(),
    ) {
        Text(
            text = "Favorites",
            style = MaterialTheme.typography.headlineMedium,
            color = AeroColors.textPrimary,
            modifier = Modifier.padding(20.dp),
        )

        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            FavoritesPage.entries.forEach { candidate ->
                val selected = candidate == page
                Text(
                    text = candidate.label,
                    color = if (selected) AeroColors.surfaceCharcoal else AeroColors.textPrimary,
                    modifier = Modifier
                        .clip(RoundedCornerShape(50))
                        .then(if (selected) Modifier.background(AeroColors.chromeMid) else Modifier.skeuoChromePill())
                        .clickable { page = candidate }
                        .padding(horizontal = 14.dp, vertical = 8.dp),
                )
            }
        }

        when (page) {
            FavoritesPage.PHOTOS -> PhotosGrid(uiState, viewModel)
            FavoritesPage.TRACKS_ALBUMS -> TracksAndAlbumsList(uiState, viewModel)
            FavoritesPage.VIDEOS -> VideosList(uiState, viewModel)
        }
    }
}

@Composable
private fun PhotosGrid(uiState: FavoritesUiState, viewModel: FavoritesViewModel) {
    if (uiState.favoritePhotos.isEmpty()) {
        EmptyHint("No favorite photos yet")
        return
    }
    LazyVerticalGrid(columns = GridCells.Fixed(3), contentPadding = PaddingValues(4.dp)) {
        items(uiState.favoritePhotos, key = { it.id }) { photo ->
            AsyncImage(
                model = photo.contentUri,
                contentDescription = photo.displayName,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .aspectRatio(1f)
                    .padding(2.dp)
                    .clip(RoundedCornerShape(4.dp))
                    .clickable { viewModel.togglePhoto(photo) },
            )
        }
    }
}

@Composable
private fun TracksAndAlbumsList(uiState: FavoritesUiState, viewModel: FavoritesViewModel) {
    if (uiState.favoriteSongs.isEmpty() && uiState.favoriteAlbums.isEmpty()) {
        EmptyHint("No favorite tracks or albums yet")
        return
    }
    LazyColumn(contentPadding = PaddingValues(horizontal = 16.dp, vertical = 4.dp)) {
        if (uiState.favoriteAlbums.isNotEmpty()) {
            item { SectionLabel("Albums") }
            itemsIndexed(uiState.favoriteAlbums, key = { _, item -> "album_${item.id}" }) { _, album ->
                Row(modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp), verticalAlignment = Alignment.CenterVertically) {
                    AsyncImage(
                        model = album.artworkUri,
                        contentDescription = null,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.size(48.dp).clip(RoundedCornerShape(6.dp)).skeuoInsetSurface(RoundedCornerShape(6.dp)),
                    )
                    Column(modifier = Modifier.weight(1f).padding(start = 12.dp)) {
                        Text(album.title, color = AeroColors.textPrimary, maxLines = 1, overflow = TextOverflow.Ellipsis)
                        Text(album.artist, color = AeroColors.textSecondary, style = MaterialTheme.typography.bodyMedium)
                    }
                    FavoriteHeartButton(isFavorite = true, onToggle = { viewModel.toggleAlbum(album) })
                }
            }
        }
        if (uiState.favoriteSongs.isNotEmpty()) {
            item { SectionLabel("Tracks") }
            itemsIndexed(uiState.favoriteSongs, key = { _, item -> "song_${item.id}" }) { _, song ->
                Row(modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp), verticalAlignment = Alignment.CenterVertically) {
                    AsyncImage(
                        model = MusicStoreRepository.albumArtUriFor(song.albumId),
                        contentDescription = null,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.size(44.dp).clip(RoundedCornerShape(6.dp)).skeuoInsetSurface(RoundedCornerShape(6.dp)),
                    )
                    Column(modifier = Modifier.weight(1f).padding(start = 12.dp)) {
                        Text(song.title, color = AeroColors.textPrimary, maxLines = 1, overflow = TextOverflow.Ellipsis)
                        Text("${song.artist} • ${DurationFormatter.format(song.durationMs)}", color = AeroColors.textSecondary, style = MaterialTheme.typography.bodyMedium)
                    }
                    FavoriteHeartButton(isFavorite = true, onToggle = { viewModel.toggleSong(song) })
                }
            }
        }
    }
}

@Composable
private fun VideosList(uiState: FavoritesUiState, viewModel: FavoritesViewModel) {
    if (uiState.favoriteVideos.isEmpty()) {
        EmptyHint("No favorite videos yet")
        return
    }
    LazyColumn(contentPadding = PaddingValues(horizontal = 16.dp, vertical = 4.dp)) {
        itemsIndexed(uiState.favoriteVideos, key = { _, item -> item.id }) { _, video ->
            Row(modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp), verticalAlignment = Alignment.CenterVertically) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(video.title, color = AeroColors.textPrimary, maxLines = 1, overflow = TextOverflow.Ellipsis)
                    Text(DurationFormatter.format(video.durationMs), color = AeroColors.textSecondary, style = MaterialTheme.typography.bodyMedium)
                }
                FavoriteHeartButton(isFavorite = true, onToggle = { viewModel.toggleVideo(video) })
            }
        }
    }
}

@Composable
private fun SectionLabel(text: String) {
    Text(
        text = text.uppercase(),
        style = MaterialTheme.typography.labelSmall,
        color = AeroColors.textSecondary,
        modifier = Modifier.padding(top = 14.dp, bottom = 4.dp),
    )
}

@Composable
private fun EmptyHint(text: String) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text(text, color = AeroColors.textSecondary)
    }
}
