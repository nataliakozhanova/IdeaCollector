package com.example.ideacollector.di

import com.example.ideacollector.notes.presentation.viewmodel.NotesViewModel
import com.example.ideacollector.settings.presentation.viewmodel.SettingsViewModel
import org.koin.android.ext.koin.androidApplication
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val viewModelModule = module {
    viewModel {
        NotesViewModel(get())
    }

    viewModel{
        SettingsViewModel(get(), get())
    }
}