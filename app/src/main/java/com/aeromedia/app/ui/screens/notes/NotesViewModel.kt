package com.aeromedia.app.ui.screens.notes

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.aeromedia.app.data.db.AppDatabase
import com.aeromedia.app.data.db.NoteEntity
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

/** Real, persisted notes via Room — every note survives an app restart,
 *  nothing here is sample/in-memory-only data. */
class NotesViewModel(application: Application) : AndroidViewModel(application) {

    private val dao = AppDatabase.getInstance(application).noteDao()

    val notes: StateFlow<List<NoteEntity>> = dao.observeAll()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _openNoteId = MutableStateFlow<Long?>(null)
    val openNoteId: StateFlow<Long?> = _openNoteId

    fun openNote(id: Long?) {
        _openNoteId.value = id
    }

    fun createNote() {
        viewModelScope.launch {
            val newId = dao.upsert(NoteEntity(title = "New Note", body = "", updatedAtMs = System.currentTimeMillis()))
            _openNoteId.value = newId
        }
    }

    fun save(note: NoteEntity) {
        viewModelScope.launch {
            dao.upsert(note.copy(updatedAtMs = System.currentTimeMillis()))
        }
    }

    fun delete(note: NoteEntity) {
        viewModelScope.launch {
            dao.delete(note)
            _openNoteId.value = null
        }
    }

    suspend fun getById(id: Long): NoteEntity? = dao.getById(id)
}
