package com.aeromedia.app.data.db

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface FavoriteDao {
    @Query("SELECT * FROM favorites WHERE kind = :kind ORDER BY addedAtMs DESC")
    fun observeByKind(kind: FavoriteKind): Flow<List<FavoriteEntity>>

    @Query("SELECT EXISTS(SELECT 1 FROM favorites WHERE kind = :kind AND mediaId = :mediaId)")
    fun observeIsFavorite(kind: FavoriteKind, mediaId: Long): Flow<Boolean>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun add(favorite: FavoriteEntity)

    @Delete
    suspend fun remove(favorite: FavoriteEntity)

    @Query("DELETE FROM favorites WHERE kind = :kind AND mediaId = :mediaId")
    suspend fun removeById(kind: FavoriteKind, mediaId: Long)
}
