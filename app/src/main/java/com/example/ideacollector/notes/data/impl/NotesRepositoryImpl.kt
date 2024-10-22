package com.example.ideacollector.notes.data.impl

import com.example.ideacollector.notes.data.converters.NoteDbConverter
import com.example.ideacollector.notes.data.db.NoteEntity
import com.example.ideacollector.notes.data.db.NotesDatabase
import com.example.ideacollector.notes.domain.api.NotesRepository
import com.example.ideacollector.notes.domain.models.Note
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class NotesRepositoryImpl(
    private val notesDatabase: NotesDatabase,
    private val noteDbConverter: NoteDbConverter
) : NotesRepository {
    override fun getAllNotes(): Flow<List<Note>> = flow {
            val allNotes = notesDatabase.notesDao().getAllNotes()
            emit(convertToNote(allNotes))
    }

    override suspend fun addNewNote(note: Note) {
        val noteEntity = convertToNoteEntity(note)
        notesDatabase.notesDao().addNewNote(noteEntity)
    }

    override suspend fun deleteNote(id: Int) {
        notesDatabase.notesDao().deleteNote(id)
    }

    private fun convertToNoteEntity(note: Note): NoteEntity {
        return noteDbConverter.map(note)
    }

    private fun convertToNote(notes: List<NoteEntity>): List<Note> {
        return notes.map { note -> noteDbConverter.map(note) }
    }
}