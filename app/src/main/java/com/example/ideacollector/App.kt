package com.example.ideacollector

import android.app.Application
import com.example.ideacollector.di.dataModule
import com.example.ideacollector.di.dataStoreModule
import com.example.ideacollector.di.interactorModule
import com.example.ideacollector.di.managerModule
import com.example.ideacollector.di.repositoryModule
import com.example.ideacollector.di.viewModelModule
import com.example.ideacollector.managers.ThemeManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import org.koin.android.ext.android.getKoin
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.GlobalContext.startKoin

class App : Application() {

    private val applicationScope = CoroutineScope(Dispatchers.Default + SupervisorJob())

    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidContext(this@App)
            modules(
                dataModule,
                viewModelModule,
                interactorModule,
                repositoryModule,
                dataStoreModule,
                managerModule
            )
        }
        val themeManager: ThemeManager = getKoin().get()

//        themeManager.applyTheme()
//
//        applicationScope.launch {
//            themeManager.currentTheme.collect { theme ->
//                themeManager.setAppTheme(theme)
//            }
//        }

        CoroutineScope(Dispatchers.Default).launch {
            themeManager.applyTheme()
        }
    }
}

