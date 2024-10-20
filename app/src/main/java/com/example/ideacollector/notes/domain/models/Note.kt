package com.example.ideacollector.notes.domain.models

class Note(
    val id: Int,
    val priority: Int,
    val noteText: String,
    val noteData: String
) {
    constructor(
        priority: Int,
        noteText: String,
        noteData: String,
    ) : this(
        id = 0,
        priority = priority,
        noteText = noteText,
        noteData = noteData
    )
}