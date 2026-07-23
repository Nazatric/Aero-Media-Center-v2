package com.aeromedia.app.data.media

import android.content.ContentUris
import android.content.Context
import android.provider.MediaStore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/** Scans the device's real photo library via MediaStore.Images. Requires
 *  READ_MEDIA_IMAGES (API 33+) or READ_EXTERNAL_STORAGE (below). */
class PictureStoreRepository(private val context: Context) {

    suspend fun loadAllPhotos(excludedFolders: Set<String> = emptySet()): List<Photo> = withContext(Dispatchers.IO) {
        queryPhotos().filter { it.bucketName !in excludedFolders }
    }

    suspend fun loadDistinctFolders(): List<String> = withContext(Dispatchers.IO) {
        queryPhotos().map { it.bucketName }.distinct().sorted()
    }

    private fun queryPhotos(): List<Photo> {
        val photos = mutableListOf<Photo>()
        val projection = arrayOf(
            MediaStore.Images.Media._ID,
            MediaStore.Images.Media.DISPLAY_NAME,
            MediaStore.Images.Media.DATE_TAKEN,
            MediaStore.Images.Media.BUCKET_DISPLAY_NAME,
        )
        context.contentResolver.query(
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
            projection,
            null,
            null,
            "${MediaStore.Images.Media.DATE_TAKEN} DESC",
        )?.use { cursor ->
            val idCol = cursor.getColumnIndexOrThrow(MediaStore.Images.Media._ID)
            val nameCol = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DISPLAY_NAME)
            val dateCol = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATE_TAKEN)
            val bucketCol = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.BUCKET_DISPLAY_NAME)
            while (cursor.moveToNext()) {
                val id = cursor.getLong(idCol)
                photos += Photo(
                    id = id,
                    displayName = cursor.getString(nameCol) ?: "Untitled",
                    dateTakenMs = cursor.getLong(dateCol).takeIf { it > 0 },
                    bucketName = cursor.getString(bucketCol) ?: "Unknown folder",
                    contentUri = ContentUris.withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, id),
                )
            }
        }
        return photos
    }
}
