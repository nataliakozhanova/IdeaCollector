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
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import org.koin.android.ext.android.getKoin
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.GlobalContext.startKoin

class App : Application() {

    private val coroutineScope = CoroutineScope(Dispatchers.Default)

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

        coroutineScope.launch {
            themeManager.collectThemeSettings()
        }
    }

    override fun onTerminate() {
        super.onTerminate()
        coroutineScope.cancel()
    }
}

