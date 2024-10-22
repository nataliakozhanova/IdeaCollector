package com.example.ideacollector.di

import androidx.room.Room
import com.example.ideacollector.notes.data.db.NotesDatabase
import com.google.gson.Gson
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val dataModule = module {
    single {
        Room.databaseBuilder(androidContext(), NotesDatabase::class.java, "database.db")
            .fallbackToDestructiveMigration()
            .build()
    }

    factory { Gson() }
}
