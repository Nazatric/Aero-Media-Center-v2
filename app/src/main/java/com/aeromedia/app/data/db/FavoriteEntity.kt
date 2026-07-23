package com.aeromedia.app.data.db

import androidx.room.Entity

enum class FavoriteKind { SONG, ALBUM, VIDEO, PHOTO }

/** One favorited piece of real media, keyed by its MediaStore id. The
 *  Favorites screen's three pages (Photos / Tracks & Albums / Videos) are
 *  just filtered views over this one table. */
@Entity(tableName = "favorites", primaryKeys = ["kind", "mediaId"])
data class FavoriteEntity(
    val kind: FavoriteKind,
    val mediaId: Long,
    val addedAtMs: Long,
)
