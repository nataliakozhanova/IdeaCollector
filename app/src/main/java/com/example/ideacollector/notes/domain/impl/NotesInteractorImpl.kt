package com.example.ideacollector.notes.domain.impl

import com.example.ideacollector.notes.domain.api.NotesInteractor
import com.example.ideacollector.notes.domain.api.NotesRepository
import com.example.ideacollector.notes.domain.models.Note
import kotlinx.coroutines.flow.Flow

class NotesInteractorImpl(private val notesRepository: NotesRepository) : NotesInteractor {
    override fun getAllNotes(): Flow<List<Note>> {
        return notesRepository.getAllNotes()
    }

    override suspend fun addNewNote(note: Note) : Int {
        return notesRepository.addNewNote(note)
    }

    override suspend fun deleteNote(id: Int) {
        notesRepository.deleteNote(id)
    }

}