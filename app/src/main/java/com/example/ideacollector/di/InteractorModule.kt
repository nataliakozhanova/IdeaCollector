package com.example.ideacollector.di

import com.example.ideacollector.notes.domain.api.NotesInteractor
import com.example.ideacollector.notes.domain.impl.NotesInteractorImpl
import com.example.ideacollector.settings.domain.api.SettingsInteractor
import com.example.ideacollector.settings.domain.impl.SettingsInteractorImpl
import org.koin.dsl.module

val interactorModule = module {
    factory<NotesInteractor> {
        NotesInteractorImpl(get())
    }

    factory<SettingsInteractor> {
        SettingsInteractorImpl(get())
    }
}