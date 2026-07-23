package com.aeromedia.app.data.media

import android.content.ContentUris
import android.content.Context
import android.net.Uri
import android.provider.MediaStore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Scans the device's real audio library via the MediaStore content provider.
 * Every Song/Album returned here comes from an actual file on the device —
 * title, artist, album, duration, track number, real album art URI. Callers
 * must already hold READ_MEDIA_AUDIO (API 33+) or READ_EXTERNAL_STORAGE
 * (below) — see ui/permissions/MediaPermissions.kt.
 */
class MusicStoreRepository(private val context: Context) {

    /** Every track on the device, alphabetical by title, minus anything in an
     *  excluded folder (see SettingsRepository). This is what the Music
     *  screen's Tracks pane shows. */
    suspend fun loadAllSongs(excludedFolders: Set<String> = emptySet()): List<Song> = withContext(Dispatchers.IO) {
        querySongs(selection = "${MediaStore.Audio.Media.IS_MUSIC} = 1", sortOrder = "${MediaStore.Audio.Media.TITLE} ASC")
            .filter { it.bucketName !in excludedFolders }
    }

    /** Distinct folder names your music lives in, for the Settings screen's
     *  "Exclude music folders" list. */
    suspend fun loadDistinctFolders(): List<String> = withContext(Dispatchers.IO) {
        querySongs(selection = "${MediaStore.Audio.Media.IS_MUSIC} = 1", sortOrder = null)
            .map { it.bucketName }
            .distinct()
            .sorted()
    }

    /** Every album on the device, alphabetical by title. */
    suspend fun loadAlbums(): List<Album> = withContext(Dispatchers.IO) {
        val albums = mutableListOf<Album>()
        val projection = arrayOf(
            MediaStore.Audio.Albums._ID,
            MediaStore.Audio.Albums.ALBUM,
            MediaStore.Audio.Albums.ARTIST,
            MediaStore.Audio.Albums.FIRST_YEAR,
            MediaStore.Audio.Albums.NUMBER_OF_SONGS,
        )
        context.contentResolver.query(
            MediaStore.Audio.Albums.EXTERNAL_CONTENT_URI,
            projection,
            null,
            null,
            "${MediaStore.Audio.Albums.ALBUM} ASC",
        )?.use { cursor ->
            val idCol = cursor.getColumnIndexOrThrow(MediaStore.Audio.Albums._ID)
            val albumCol = cursor.getColumnIndexOrThrow(MediaStore.Audio.Albums.ALBUM)
            val artistCol = cursor.getColumnIndexOrThrow(MediaStore.Audio.Albums.ARTIST)
            val yearCol = cursor.getColumnIndexOrThrow(MediaStore.Audio.Albums.FIRST_YEAR)
            val countCol = cursor.getColumnIndexOrThrow(MediaStore.Audio.Albums.NUMBER_OF_SONGS)
            while (cursor.moveToNext()) {
                val id = cursor.getLong(idCol)
                albums += Album(
                    id = id,
                    title = cursor.getString(albumCol) ?: "Unknown Album",
                    artist = cursor.getString(artistCol) ?: "Unknown Artist",
                    year = cursor.getInt(yearCol).takeIf { it > 0 },
                    songCount = cursor.getInt(countCol),
                    artworkUri = albumArtUriFor(id),
                )
            }
        }
        albums
    }

    /** All tracks belonging to one album, in track order — used by the
     *  Albums pane's "play album" and by Cover Flow's per-album drill-down. */
    suspend fun loadSongsForAlbum(albumId: Long): List<Song> = withContext(Dispatchers.IO) {
        querySongs(
            selection = "${MediaStore.Audio.Media.ALBUM_ID} = ? AND ${MediaStore.Audio.Media.IS_MUSIC} = 1",
            selectionArgs = arrayOf(albumId.toString()),
            sortOrder = "${MediaStore.Audio.Media.TRACK} ASC",
        )
    }

    /** All artists on the device, alphabetical. */
    suspend fun loadArtists(): List<Artist> = withContext(Dispatchers.IO) {
        val artists = mutableListOf<Artist>()
        val projection = arrayOf(
            MediaStore.Audio.Artists._ID,
            MediaStore.Audio.Artists.ARTIST,
            MediaStore.Audio.Artists.NUMBER_OF_ALBUMS,
            MediaStore.Audio.Artists.NUMBER_OF_TRACKS,
        )
        context.contentResolver.query(
            MediaStore.Audio.Artists.EXTERNAL_CONTENT_URI,
            projection,
            null,
            null,
            "${MediaStore.Audio.Artists.ARTIST} ASC",
        )?.use { cursor ->
            val idCol = cursor.getColumnIndexOrThrow(MediaStore.Audio.Artists._ID)
            val nameCol = cursor.getColumnIndexOrThrow(MediaStore.Audio.Artists.ARTIST)
            val albumCountCol = cursor.getColumnIndexOrThrow(MediaStore.Audio.Artists.NUMBER_OF_ALBUMS)
            val trackCountCol = cursor.getColumnIndexOrThrow(MediaStore.Audio.Artists.NUMBER_OF_TRACKS)
            while (cursor.moveToNext()) {
                artists += Artist(
                    id = cursor.getLong(idCol),
                    name = cursor.getString(nameCol) ?: "Unknown Artist",
                    albumCount = cursor.getInt(albumCountCol),
                    trackCount = cursor.getInt(trackCountCol),
                )
            }
        }
        artists
    }

    private fun querySongs(
        selection: String?,
        selectionArgs: Array<String>? = null,
        sortOrder: String?,
    ): List<Song> {
        val songs = mutableListOf<Song>()
        val projection = arrayOf(
            MediaStore.Audio.Media._ID,
            MediaStore.Audio.Media.TITLE,
            MediaStore.Audio.Media.ARTIST,
            MediaStore.Audio.Media.ALBUM,
            MediaStore.Audio.Media.ALBUM_ID,
            MediaStore.Audio.Media.DURATION,
            MediaStore.Audio.Media.TRACK,
            MediaStore.Audio.Media.BUCKET_DISPLAY_NAME,
        )
        context.contentResolver.query(
            MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
            projection,
            selection,
            selectionArgs,
            sortOrder,
        )?.use { cursor ->
            val idCol = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media._ID)
            val titleCol = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.TITLE)
            val artistCol = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST)
            val albumCol = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM)
            val albumIdCol = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM_ID)
            val durationCol = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION)
            val trackCol = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.TRACK)
            val bucketCol = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.BUCKET_DISPLAY_NAME)
            while (cursor.moveToNext()) {
                val id = cursor.getLong(idCol)
                songs += Song(
                    id = id,
                    title = cursor.getString(titleCol) ?: "Unknown Title",
                    artist = cursor.getString(artistCol) ?: "Unknown Artist",
                    album = cursor.getString(albumCol) ?: "Unknown Album",
                    albumId = cursor.getLong(albumIdCol),
                    durationMs = cursor.getLong(durationCol),
                    // MediaStore packs disc number into the high digits for
                    // multi-disc albums (e.g. 1005 = disc 1, track 5) — mask
                    // it off since we only show track order, not disc grouping.
                    trackNumber = cursor.getInt(trackCol) % 1000,
                    bucketName = cursor.getString(bucketCol) ?: "Unknown folder",
                    contentUri = ContentUris.withAppendedId(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, id),
                )
            }
        }
        return songs
    }

    companion object {
        private val ALBUM_ART_CONTENT_URI: Uri = Uri.parse("content://media/external/audio/albumart")

        /** The standard per-album-art content URI. Real device artwork when
         *  the album has embedded/cached art; Coil quietly falls back to a
         *  placeholder (see ui/components) when it doesn't. */
        fun albumArtUriFor(albumId: Long): Uri = ContentUris.withAppendedId(ALBUM_ART_CONTENT_URI, albumId)
    }
}
