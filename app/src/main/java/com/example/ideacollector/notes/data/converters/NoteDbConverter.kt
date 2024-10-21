package com.example.ideacollector.notes.data.converters

import com.example.ideacollector.notes.data.db.NoteEntity
import com.example.ideacollector.notes.domain.models.Note
import com.google.gson.Gson

class NoteDbConverter()  {
    fun map(note: Note) : NoteEntity {
        return NoteEntity(
            note.id,
            note.priority,
            note.noteText,
            note.noteData
        )
    }

    fun map(noteEntity: NoteEntity) : Note {
        return Note(
            noteEntity.id,
            noteEntity.priority,
            noteEntity.noteText,
            noteEntity.noteData
        )
    }
}