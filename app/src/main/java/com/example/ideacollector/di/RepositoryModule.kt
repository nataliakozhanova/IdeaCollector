package com.example.ideacollector.di

import com.example.ideacollector.notes.data.converters.NoteDbConverter
import com.example.ideacollector.notes.data.repo.RoomNotesRepository
import com.example.ideacollector.notes.domain.api.NotesRepository
import org.koin.dsl.module

val repositoryModule = module {
    factory { NoteDbConverter() }

    single<NotesRepository> {
        RoomNotesRepository(get(), get())
    }
}