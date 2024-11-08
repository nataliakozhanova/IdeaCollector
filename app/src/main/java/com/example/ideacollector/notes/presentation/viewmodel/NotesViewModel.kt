package com.example.ideacollector.notes.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ideacollector.notes.domain.api.NotesInteractor
import com.example.ideacollector.notes.domain.models.Note
import com.example.ideacollector.notes.domain.models.Priority
import com.example.ideacollector.notes.presentation.models.NotesState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class NotesViewModel(private val notesInteractor: NotesInteractor) : ViewModel() {
    private val _priority = MutableStateFlow(Priority.LOW)
    val priority: StateFlow<Priority> get() = _priority

    private val _savedNote = MutableStateFlow(Note())
    // val savedNote: StateFlow<Note> get() = _savedNote

    val allNotes: StateFlow<NotesState> = notesInteractor
        .getAllNotes()
        .map { notes ->
            if (notes.isEmpty()) {
                NotesState.Empty
            } else {
                NotesState.Content(notes)
            }
        }.stateIn(
            viewModelScope,
            SharingStarted.Lazily,
            NotesState.Empty
        )

    fun saveNoteIfValid(priority: String, noteText: String, noteDate: String) {
        if(noteText.isNotEmpty()) {
            _savedNote.value = Note(0, priority, noteText, noteDate)
            val noteToAdd = _savedNote.value
            viewModelScope.launch(Dispatchers.IO) {
                notesInteractor.addNewNote(noteToAdd)
            }
        }
    }

    fun deleteNote(noteToDelete: Note) {
        viewModelScope.launch(Dispatchers.IO) {
            notesInteractor.deleteNote(noteToDelete.id)
        }
    }

    fun editNote(id: Int, priority: String, noteText: String, noteDate: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val editedNote = Note(id, priority, noteText, noteDate)
            notesInteractor.editNote(editedNote)
        }
    }

    fun updatePriority() {
        _priority.value = when (_priority.value) {
            Priority.LOW -> Priority.MEDIUM
            Priority.MEDIUM -> Priority.HIGH
            Priority.HIGH -> Priority.LOW
        }
    }
}