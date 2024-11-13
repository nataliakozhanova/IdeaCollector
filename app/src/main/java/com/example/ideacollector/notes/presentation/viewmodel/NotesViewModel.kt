package com.example.ideacollector.notes.presentation.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
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

    private val _editedPriority = MutableLiveData<Priority>()
    val editedPriority: LiveData<Priority> get() = _editedPriority

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
        if (noteText.isNotEmpty()) {
            val noteToAdd = Note(0, priority, noteText, noteDate)
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

    fun editNote(oldNote: Note, newNote: Note) {
        if (newNote.text.isNotEmpty() && (oldNote.text != newNote.text || oldNote.priority != newNote.priority)) {
            saveEditedNote(newNote)
        }
    }

    private fun saveEditedNote(editedNote: Note) {
        viewModelScope.launch(Dispatchers.IO) {
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

    fun setInitialPriority(priority: Priority) {
        _editedPriority.value = priority
    }

    fun editPriority() {
        _editedPriority.value = when (_editedPriority.value) {
            Priority.LOW -> Priority.MEDIUM
            Priority.MEDIUM -> Priority.HIGH
            Priority.HIGH -> Priority.LOW
            else -> Priority.LOW
        }
    }
}