package com.example.ideacollector.notes.domain.models

data class Note(
    var id: Int = 0,
    val priority: String,
    val noteText: String,
    var noteData: String
)