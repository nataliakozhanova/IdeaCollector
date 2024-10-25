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
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class NotesViewModel(private val notesInteractor: NotesInteractor) : ViewModel() {
    private val _priority = MutableStateFlow(Priority.LOW)
    val priority: StateFlow<Priority> get() = _priority

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

    fun saveNote(priority: String, noteText: String, noteData: String) {
        var noteToAdd = Note(0, priority, noteText, noteData)
        viewModelScope.launch(Dispatchers.IO) {
            noteToAdd.id = notesInteractor.addNewNote(noteToAdd)
        }
    }

    fun deleteNote(noteToDelete: Note) {
        viewModelScope.launch(Dispatchers.IO) {
            notesInteractor.deleteNote(noteToDelete.id)
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