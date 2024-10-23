package com.example.ideacollector.notes.data.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "notes_table")
data class NoteEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val priority: String,
    val noteText: String,
    val noteData: String
)