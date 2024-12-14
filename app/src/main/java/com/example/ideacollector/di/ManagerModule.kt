package com.example.ideacollector.di

import com.example.ideacollector.managers.ThemeManager
import org.koin.android.ext.koin.androidApplication
import org.koin.dsl.module

val managerModule = module {
    single { ThemeManager(get(), androidApplication()) }
}