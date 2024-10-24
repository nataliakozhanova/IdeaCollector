package com.example.ideacollector.notes.data.db

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(version = 2, entities = [NoteEntity::class], exportSchema = false)
abstract class NotesDatabase : RoomDatabase() {
    abstract fun notesDao(): NotesDao
}