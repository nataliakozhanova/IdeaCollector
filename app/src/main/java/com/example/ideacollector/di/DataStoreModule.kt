package com.example.ideacollector.di

import android.content.Context
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import org.koin.dsl.module

// Имя хранилища
private const val DATASTORE_NAME = "idea_collector_preferences"

// Делегат для DataStore
val Context.dataStore by preferencesDataStore(name = DATASTORE_NAME)

// Модуль для DataStore
val dataStoreModule = module {
    single { provideDataStore(get()) }
}

fun provideDataStore(context: Context): androidx.datastore.core.DataStore<Preferences> {
    return context.dataStore
}