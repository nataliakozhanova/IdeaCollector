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
import kotlinx.coroutines.launch

class NotesViewModel(private val notesInteractor: NotesInteractor) : ViewModel() {
    private val stateNotesLiveData = MutableLiveData<NotesState>()
    fun observeNotesState(): LiveData<NotesState> = stateNotesLiveData

    private val _priority = MutableLiveData(Priority.LOW)
    val priority: LiveData<Priority> get() = _priority

    private var currentNotes: ArrayList<Note> = ArrayList()

    fun getNotes() {
        viewModelScope.launch(Dispatchers.IO) {
            notesInteractor
                .getAllNotes()
                .collect { notes ->
                    currentNotes.clear()
                    if (notes.isEmpty()) {
                        renderNotesState(NotesState.Empty)
                    } else {
                        currentNotes.addAll(notes)
                        renderNotesState(NotesState.Content(currentNotes))
                    }
                }
        }
    }

    fun saveNote(priority: String, noteText: String, noteData: String) {
        viewModelScope.launch(Dispatchers.IO) {
            var noteToAdd = Note(0, priority, noteText, noteData)
            noteToAdd.id = notesInteractor.addNewNote(noteToAdd)
            currentNotes.add(noteToAdd)
            renderNotesState(NotesState.Content(currentNotes))
        }
    }

    fun updatePriority() {
        _priority.value = when (_priority.value) {
            Priority.LOW -> Priority.MEDIUM
            Priority.MEDIUM -> Priority.HIGH
            Priority.HIGH -> Priority.LOW
            else -> Priority.LOW
        }
    }

    private fun renderNotesState(notesState: NotesState) {
        stateNotesLiveData.postValue(notesState)
    }
}