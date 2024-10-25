package com.example.ideacollector.notes.data.repo

import com.example.ideacollector.notes.data.converters.NoteDbConverter
import com.example.ideacollector.notes.data.db.NoteEntity
import com.example.ideacollector.notes.data.db.NotesDatabase
import com.example.ideacollector.notes.domain.api.NotesRepository
import com.example.ideacollector.notes.domain.models.Note
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class RoomNotesRepository(
    private val notesDatabase: NotesDatabase,
    private val noteDbConverter: NoteDbConverter,
) : NotesRepository {
    override fun getAllNotes(): Flow<List<Note>> {
        return notesDatabase.notesDao().getAllNotes()
            .map {
                note -> convertToNote(note)
            }
    }

    override suspend fun addNewNote(note: Note) : Int {
        val noteEntity = convertToNoteEntity(note)
        return notesDatabase.notesDao().addNewNote(noteEntity).toInt()
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