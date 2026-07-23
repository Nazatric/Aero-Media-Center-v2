package com.aeromedia.app.ui.screens.favorites

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.aeromedia.app.data.db.AppDatabase
import com.aeromedia.app.data.db.FavoriteEntity
import com.aeromedia.app.data.db.FavoriteKind
import com.aeromedia.app.data.media.Album
import com.aeromedia.app.data.media.MusicStoreRepository
import com.aeromedia.app.data.media.PictureStoreRepository
import com.aeromedia.app.data.media.Photo
import com.aeromedia.app.data.media.Song
import com.aeromedia.app.data.media.VideoItem
import com.aeromedia.app.data.media.VideoStoreRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch

data class FavoritesUiState(
    val favoriteSongs: List<Song> = emptyList(),
    val favoriteAlbums: List<Album> = emptyList(),
    val favoritePhotos: List<Photo> = emptyList(),
    val favoriteVideos: List<VideoItem> = emptyList(),
)

/**
 * Favorites are real Room rows (kind + mediaId), cross-referenced against
 * the real MediaStore libraries so this screen shows actual titles and
 * artwork rather than bare ids. Re-scans the libraries each time this
 * screen loads — simple and correct; an indexed join would be the Phase 2
 * optimization for very large libraries.
 */
class FavoritesViewModel(application: Application) : AndroidViewModel(application) {

    private val favoriteDao = AppDatabase.getInstance(application).favoriteDao()
    private val musicRepo = MusicStoreRepository(application)
    private val videoRepo = VideoStoreRepository(application)
    private val pictureRepo = PictureStoreRepository(application)

    private val _uiState = MutableStateFlow(FavoritesUiState())
    val uiState: StateFlow<FavoritesUiState> = _uiState.asStateFlow()

    fun refresh() {
        viewModelScope.launch {
            val songFavoriteIds = firstIds(FavoriteKind.SONG)
            val albumFavoriteIds = firstIds(FavoriteKind.ALBUM)
            val videoFavoriteIds = firstIds(FavoriteKind.VIDEO)
            val photoFavoriteIds = firstIds(FavoriteKind.PHOTO)

            val allSongs = musicRepo.loadAllSongs()
            val allAlbums = musicRepo.loadAlbums()
            val allVideos = videoRepo.loadAllVideos()
            val allPhotos = pictureRepo.loadAllPhotos()

            _uiState.value = FavoritesUiState(
                favoriteSongs = allSongs.filter { it.id in songFavoriteIds },
                favoriteAlbums = allAlbums.filter { it.id in albumFavoriteIds },
                favoriteVideos = allVideos.filter { it.id in videoFavoriteIds },
                favoritePhotos = allPhotos.filter { it.id in photoFavoriteIds },
            )
        }
    }

    fun toggleSong(song: Song) = toggle(FavoriteKind.SONG, song.id)
    fun toggleAlbum(album: Album) = toggle(FavoriteKind.ALBUM, album.id)
    fun toggleVideo(video: VideoItem) = toggle(FavoriteKind.VIDEO, video.id)
    fun togglePhoto(photo: Photo) = toggle(FavoriteKind.PHOTO, photo.id)

    private fun toggle(kind: FavoriteKind, mediaId: Long) {
        viewModelScope.launch {
            val currentlyFavorite = mediaId in firstIds(kind)
            if (currentlyFavorite) {
                favoriteDao.removeById(kind, mediaId)
            } else {
                favoriteDao.add(FavoriteEntity(kind, mediaId, System.currentTimeMillis()))
            }
            refresh()
        }
    }

    private suspend fun firstIds(kind: FavoriteKind): Set<Long> {
        val list = favoriteDao.observeByKind(kind).firstOrNull() ?: emptyList()
        return list.map { it.mediaId }.toSet()
    }
}
