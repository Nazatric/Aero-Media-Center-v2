package com.aeromedia.app.data.media

import android.net.Uri

/** A single scanned audio track, read from MediaStore.Audio — never sample data. */
data class Song(
    val id: Long,
    val title: String,
    val artist: String,
    val album: String,
    val albumId: Long,
    val durationMs: Long,
    val trackNumber: Int,
    val bucketName: String,
    val contentUri: Uri,
)

/** An album, grouping songs — what the Albums pane and Cover Flow browse by default. */
data class Album(
    val id: Long,
    val title: String,
    val artist: String,
    val year: Int?,
    val songCount: Int,
    val artworkUri: Uri,
)

/** An artist, as grouped by MediaStore.Audio.Artists. */
data class Artist(
    val id: Long,
    val name: String,
    val albumCount: Int,
    val trackCount: Int,
)

/** A single scanned video, read from MediaStore.Video. */
data class VideoItem(
    val id: Long,
    val title: String,
    val durationMs: Long,
    val sizeBytes: Long,
    val dateAddedSec: Long,
    val bucketName: String,
    val contentUri: Uri,
)

/** A single scanned photo, read from MediaStore.Images. */
data class Photo(
    val id: Long,
    val displayName: String,
    val dateTakenMs: Long?,
    val bucketName: String,
    val contentUri: Uri,
)
