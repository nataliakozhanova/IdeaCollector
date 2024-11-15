package com.example.ideacollector

import android.app.Application
import android.content.res.Configuration
import androidx.appcompat.app.AppCompatDelegate
import com.example.ideacollector.di.PREFERENCES
import com.example.ideacollector.managers.ThemeManager
import com.example.ideacollector.di.dataModule
import com.example.ideacollector.di.interactorModule
import com.example.ideacollector.di.repositoryModule
import com.example.ideacollector.di.viewModelModule
import com.example.ideacollector.settings.data.storage.THEME_KEY
import com.example.ideacollector.settings.domain.models.Theme
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.GlobalContext.startKoin

class App : Application() {
    private var darkTheme: Theme = Theme.LIGHT

    // TODO - перенести логику в ThemeManager
//        val themeManager = ThemeManager()
//        themeManager.currentTheme.collect {
//
//        }

    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidContext(this@App)
            modules(dataModule, viewModelModule, interactorModule, repositoryModule)
        }

        darkTheme = getCurrentTheme()

        switchTheme(darkTheme)
    }

    fun switchTheme(theme: Theme) {
        darkTheme = theme
        AppCompatDelegate.setDefaultNightMode(
            when (theme) {
                Theme.DARK -> AppCompatDelegate.MODE_NIGHT_YES
                Theme.LIGHT -> AppCompatDelegate.MODE_NIGHT_NO
            }
        )
    }

    fun isSystemDarkMode(): Theme {
        val currentNightMode = resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK
        return when (currentNightMode) {
            Configuration.UI_MODE_NIGHT_YES -> Theme.DARK
            Configuration.UI_MODE_NIGHT_NO -> Theme.LIGHT
            else -> Theme.LIGHT
        }
    }

    fun getCurrentTheme() : Theme {
        val sharedPrefs = (getSharedPreferences(PREFERENCES, MODE_PRIVATE))
        return if (sharedPrefs == null) {
            isSystemDarkMode()
        } else {
            val currentTheme: String? = sharedPrefs.getString(THEME_KEY, null)
            enumValueOf<Theme>(currentTheme ?: isSystemDarkMode().toString())
        }
    }
}