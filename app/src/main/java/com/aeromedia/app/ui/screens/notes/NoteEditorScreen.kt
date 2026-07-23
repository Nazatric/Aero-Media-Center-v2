package com.aeromedia.app.ui.screens.notes

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.aeromedia.app.data.db.NoteEntity
import com.aeromedia.app.ui.theme.AeroColors
import com.aeromedia.app.ui.theme.aeroSkyBackground
import com.aeromedia.app.ui.theme.skeuoLeatherHeader
import com.aeromedia.app.ui.theme.skeuoPaper

/** Top-level Notes destination: shows the list, or the leather/paper editor
 *  when a note is open — both driven by NotesViewModel's openNoteId state. */
@Composable
fun NotesScreen(viewModel: NotesViewModel) {
    val openNoteId by viewModel.openNoteId.collectAsState()
    if (openNoteId != null) {
        NoteEditorScreen(viewModel = viewModel, noteId = openNoteId!!)
    } else {
        NotesListScreen(viewModel = viewModel)
    }
}

@Composable
private fun NotesListScreen(viewModel: NotesViewModel) {
    val notes by viewModel.notes.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .aeroSkyBackground()
            .statusBarsPadding(),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(20.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = "Notes",
                style = MaterialTheme.typography.headlineMedium,
                color = AeroColors.textPrimary,
                modifier = Modifier.weight(1f),
            )
            Icon(
                imageVector = Icons.Filled.Add,
                contentDescription = "New note",
                tint = AeroColors.textPrimary,
                modifier = Modifier.clickable { viewModel.createNote() },
            )
        }

        if (notes.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("No notes yet — tap + to write one", color = AeroColors.textSecondary)
            }
            return
        }

        LazyColumn(contentPadding = PaddingValues(horizontal = 16.dp, vertical = 4.dp)) {
            items(notes, key = { it.id }) { note ->
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 6.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .skeuoLeatherHeader()
                        .clickable { viewModel.openNote(note.id) }
                        .padding(14.dp),
                ) {
                    Text(
                        text = note.title.ifBlank { "Untitled" },
                        color = AeroColors.leatherStitch,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                    Text(
                        text = note.body.take(60).ifBlank { "No additional text" },
                        color = AeroColors.leatherStitch.copy(alpha = 0.75f),
                        style = MaterialTheme.typography.bodyMedium,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                }
            }
        }
    }
}

/** The note editor: brown leather header band + yellow ruled-paper body,
 *  matching the reference screenshot's "New Note" look. */
@Composable
private fun NoteEditorScreen(viewModel: NotesViewModel, noteId: Long) {
    var note by remember(noteId) { mutableStateOf<NoteEntity?>(null) }
    var title by remember(noteId) { mutableStateOf("") }
    var body by remember(noteId) { mutableStateOf("") }

    LaunchedEffect(noteId) {
        val loaded = viewModel.getById(noteId)
        note = loaded
        title = loaded?.title ?: ""
        body = loaded?.body ?: ""
    }

    Column(modifier = Modifier.fillMaxSize()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .skeuoLeatherHeader()
                .padding(horizontal = 16.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icon(
                imageVector = Icons.Filled.ArrowBack,
                contentDescription = "Back",
                tint = AeroColors.leatherStitch,
                modifier = Modifier.clickable {
                    note?.let { viewModel.save(it.copy(title = title, body = body)) }
                    viewModel.openNote(null)
                },
            )
            BasicTextField(
                value = title,
                onValueChange = { title = it },
                singleLine = true,
                textStyle = TextStyle(color = AeroColors.leatherStitch, fontSize = 18.sp, textAlign = androidx.compose.ui.text.style.TextAlign.Center),
                modifier = Modifier.weight(1f).padding(horizontal = 12.dp),
                decorationBox = { inner ->
                    if (title.isEmpty()) {
                        Text("New Note", color = AeroColors.leatherStitch.copy(alpha = 0.6f), textAlign = androidx.compose.ui.text.style.TextAlign.Center, modifier = Modifier.fillMaxWidth())
                    }
                    inner()
                },
            )
            Icon(
                imageVector = Icons.Filled.Delete,
                contentDescription = "Delete note",
                tint = AeroColors.leatherStitch,
                modifier = Modifier.clickable {
                    note?.let { viewModel.delete(it) }
                    viewModel.openNote(null)
                },
            )
        }

        Box(
            modifier = Modifier
                .fillMaxSize()
                .skeuoPaper(),
        ) {
            BasicTextField(
                value = body,
                onValueChange = {
                    body = it
                    note?.let { current -> viewModel.save(current.copy(title = title, body = it)) }
                },
                textStyle = TextStyle(color = AeroColors.paperInk, fontSize = 17.sp, lineHeight = 32.sp),
                modifier = Modifier
                    .fillMaxSize()
                    .padding(start = 24.dp, end = 16.dp, top = 6.dp),
            )
        }
    }
}
