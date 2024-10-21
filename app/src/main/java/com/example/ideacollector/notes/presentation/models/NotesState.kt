package com.example.ideacollector.notes.presentation.models

import com.example.ideacollector.notes.domain.models.Note

sealed interface NotesState {
    data class Content(
        val notes: List<Note>
    ) : NotesState

    data object Empty : NotesState
}