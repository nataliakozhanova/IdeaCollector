package com.example.ideacollector.di

import com.example.ideacollector.notes.domain.api.NotesInteractor
import com.example.ideacollector.notes.domain.impl.NotesInteractorImpl
import org.koin.dsl.module

val interactorModule = module {
    factory<NotesInteractor> {
        NotesInteractorImpl(get())
    }
}