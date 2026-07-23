package com.aeromedia.app.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverter
import androidx.room.TypeConverters

class Converters {
    @TypeConverter
    fun fromFavoriteKind(kind: FavoriteKind): String = kind.name

    @TypeConverter
    fun toFavoriteKind(value: String): FavoriteKind = FavoriteKind.valueOf(value)
}

@Database(
    entities = [NoteEntity::class, FavoriteEntity::class],
    version = 1,
    exportSchema = false,
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun noteDao(): NoteDao
    abstract fun favoriteDao(): FavoriteDao

    companion object {
        @Volatile private var instance: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase =
            instance ?: synchronized(this) {
                instance ?: Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "aeromedia.db",
                ).build().also { instance = it }
            }
    }
}
