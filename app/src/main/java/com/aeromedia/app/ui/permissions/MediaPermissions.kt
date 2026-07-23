package com.aeromedia.app.ui.permissions

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.content.ContextCompat

enum class MediaKind { AUDIO, VIDEO, IMAGES }

/** The correct runtime permission for a given media kind — READ_MEDIA_* from
 *  Android 13 (API 33), READ_EXTERNAL_STORAGE below that. */
fun permissionFor(kind: MediaKind): String = when {
    Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU -> when (kind) {
        MediaKind.AUDIO -> Manifest.permission.READ_MEDIA_AUDIO
        MediaKind.VIDEO -> Manifest.permission.READ_MEDIA_VIDEO
        MediaKind.IMAGES -> Manifest.permission.READ_MEDIA_IMAGES
    }
    else -> Manifest.permission.READ_EXTERNAL_STORAGE
}

fun hasMediaPermission(context: Context, kind: MediaKind): Boolean =
    ContextCompat.checkSelfPermission(context, permissionFor(kind)) == PackageManager.PERMISSION_GRANTED
