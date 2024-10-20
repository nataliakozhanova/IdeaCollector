package com.example.ideacollector.notes.domain.api

import com.example.ideacollector.notes.domain.models.Note
import kotlinx.coroutines.flow.Flow

interface NotesInteractor {
    fun getAllNotes(): Flow<List<Note>>
    suspend fun addNewNote(note: Note)
    suspend fun deleteNote(id: Int)
}