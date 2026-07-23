package com.aeromedia.app.data.db

import androidx.room.Entity
import androidx.room.PrimaryKey

/** A single note. Real, persisted, user-created content — not sample data. */
@Entity(tableName = "notes")
data class NoteEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val body: String,
    val updatedAtMs: Long,
)
