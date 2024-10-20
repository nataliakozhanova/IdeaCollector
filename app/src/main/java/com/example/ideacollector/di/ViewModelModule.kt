package com.example.ideacollector.di

import com.example.ideacollector.notes.presentation.viewmodel.NotesViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val viewModelModule = module {
    viewModel {
        NotesViewModel(get())
    }
}