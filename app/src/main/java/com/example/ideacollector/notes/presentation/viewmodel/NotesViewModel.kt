package com.example.ideacollector.notes.presentation.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ideacollector.notes.domain.api.NotesInteractor
import com.example.ideacollector.notes.domain.models.Note
import com.example.ideacollector.notes.presentation.models.NotesState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class NotesViewModel(private val notesInteractor: NotesInteractor) : ViewModel() {
    private val stateLiveData = MutableLiveData<NotesState>()
    fun observeState(): LiveData<NotesState> = stateLiveData

    fun getNotes() {
        viewModelScope.launch(Dispatchers.IO) {
            notesInteractor
                .getAllNotes()
                .collect { notes ->
                    if (notes.isEmpty()) {
                        renderNotesState(NotesState.Empty)
                    } else {
                        renderNotesState(NotesState.Content(notes))
                    }
                }
        }
    }

    fun saveNote(priority: Int, noteText: String, noteData: String) {

        val note = Note(priority, noteText, noteData)
        viewModelScope.launch(Dispatchers.IO) {
            notesInteractor.addNewNote(note)
        }
    }

    private fun renderNotesState(notesState: NotesState) {
        stateLiveData.postValue(notesState)
    }
}