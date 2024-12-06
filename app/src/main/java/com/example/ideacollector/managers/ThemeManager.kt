package com.example.ideacollector.managers

import android.content.Context
import android.content.res.Configuration
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatDelegate
import com.example.ideacollector.settings.domain.api.SettingsRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class ThemeManager(
    private val repository: SettingsRepository,
    private val context: Context,
) {

    private val handler = Handler(Looper.getMainLooper())

    private val _currentTheme = MutableStateFlow(isSystemDarkMode())
    val currentTheme: StateFlow<Boolean> get() = _currentTheme

    private fun setAppTheme(isDarkMode: Boolean) {
        handler.post {
            AppCompatDelegate.setDefaultNightMode(
                if (isDarkMode) {
                    AppCompatDelegate.MODE_NIGHT_YES
                } else {
                    AppCompatDelegate.MODE_NIGHT_NO
                }
            )
        }
    }

    private fun isSystemDarkMode(): Boolean {
        val currentNightMode =
            context.resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK
        return when (currentNightMode) {
            Configuration.UI_MODE_NIGHT_YES -> true
            Configuration.UI_MODE_NIGHT_NO -> false
            else -> false
        }
    }

    suspend fun collectThemeSettings() {
        repository.readThemeSettings()
            .collect { themeSettings ->
                _currentTheme.value = if (themeSettings.isEmpty) {
                    isSystemDarkMode()
                } else {
                    themeSettings.isDarkTheme
                }
                setAppTheme(_currentTheme.value)
            }
    }

    suspend fun saveTheme(themeSettings: Boolean) {
        repository.writeThemeSetting(themeSettings)
        _currentTheme.value = themeSettings
    }
}