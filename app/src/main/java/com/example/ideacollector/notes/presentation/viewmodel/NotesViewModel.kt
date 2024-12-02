package com.example.ideacollector.notes.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ideacollector.notes.domain.api.NotesInteractor
import com.example.ideacollector.notes.domain.models.Note
import com.example.ideacollector.notes.domain.models.Priority
import com.example.ideacollector.notes.presentation.models.NotesState
import com.example.ideacollector.util.getCurrentDateTime
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class NotesViewModel(
    private val notesInteractor: NotesInteractor
) : ViewModel() {

    private val _priority = MutableStateFlow(Priority.LOW)
    val priority: StateFlow<Priority> get() = _priority

    private val _editedPriority = MutableStateFlow(Priority.LOW)
    val editedPriority: StateFlow<Priority> get() = _editedPriority

    private val _passwordCheckResult = MutableStateFlow<Boolean?>(null)
    val passwordCheckResult: StateFlow<Boolean?> get() = _passwordCheckResult

    val isPasswordEnabled = notesInteractor.getEnablePassword().stateIn(
        viewModelScope,
        SharingStarted.Lazily,
        false
    )

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

    fun userClickedSaveButton(noteText: String) {
        if (noteText.isNotEmpty()) {
            val noteToAdd = Note(0, _priority.value.toString(), noteText, getCurrentDateTime())
            viewModelScope.launch(Dispatchers.IO) {
                notesInteractor.addNewNote(noteToAdd)
            }
        }

    }

    fun userClickedDeleteNote(noteToDelete: Note) {
        viewModelScope.launch(Dispatchers.IO) {
            notesInteractor.deleteNote(noteToDelete.id)
        }
    }

    fun userClickedEditNote(oldNote: Note, newText: String, newPriority: String) {
        val newNote = Note(oldNote.id, newPriority, newText, getCurrentDateTime())
        if (newText.isNotEmpty() && (oldNote.text != newText || oldNote.priority != newPriority)) {
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

    fun updateEditedPriority(priority: Priority) {
        _editedPriority.value = priority
    }

    fun setInitialPriority(priority: Priority) {
        _editedPriority.value = priority
    }

    fun editPriority() {
        _editedPriority.value = when (_editedPriority.value) {
            Priority.LOW -> Priority.MEDIUM
            Priority.MEDIUM -> Priority.HIGH
            Priority.HIGH -> Priority.LOW
        }
    }

    fun checkPassword(inputtedPassword: String) {
        viewModelScope.launch {
            try {
                notesInteractor.checkPassword(inputtedPassword).collect { result ->
                    _passwordCheckResult.value = result
                }
            } catch (e: Exception) {
                e.printStackTrace()
                _passwordCheckResult.value = false
            }
        }
    }

    // Сбрасываем состояние после обработки результата
    fun resetPasswordCheckResult() {
        _passwordCheckResult.value = null
    }
}