package com.example.ideacollector.notes.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface NotesDao {
    @Insert(entity = NoteEntity::class, onConflict = OnConflictStrategy.REPLACE)
    fun addNewNote(note: NoteEntity)

    @Query("SELECT * FROM notes_table ORDER BY id DESC")
    fun getAllNotes(): List<NoteEntity>

    @Query("DELETE FROM notes_table WHERE id = :id")
    fun deleteNote(id: Int)
}