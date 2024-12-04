package com.example.ideacollector.managers

import android.content.Context
import android.content.res.Configuration
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatDelegate
import com.example.ideacollector.settings.domain.api.SettingsRepository
import com.example.ideacollector.settings.domain.models.Theme
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class ThemeManager(
    private val repository: SettingsRepository,
    private val context: Context
) {

    val handler = Handler(Looper.getMainLooper())

    private val _currentTheme = MutableStateFlow(getSystemTheme())
    val currentTheme: StateFlow<Theme> get() = _currentTheme

    fun setAppTheme(theme: Theme) {
        handler.post {
            AppCompatDelegate.setDefaultNightMode(
                when (theme) {
                    Theme.DARK -> AppCompatDelegate.MODE_NIGHT_YES
                    Theme.LIGHT -> AppCompatDelegate.MODE_NIGHT_NO
                    Theme.SYSTEM -> context.resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK
                }
            )
        }
    }

    private fun getSystemTheme(): Theme {
        val currentNightMode =
            context.resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK
        return when (currentNightMode) {
            Configuration.UI_MODE_NIGHT_YES -> Theme.DARK
            Configuration.UI_MODE_NIGHT_NO -> Theme.LIGHT
            else -> Theme.LIGHT
        }
    }

    suspend fun collectTheme() {
        repository.readThemeSettings()
            .collect { savedTheme ->
                _currentTheme.value = savedTheme ?: getSystemTheme()
                setAppTheme(_currentTheme.value)
            }
    }

    suspend fun saveTheme(theme: Theme) {
        repository.writeThemeSetting(theme)
        _currentTheme.value = theme
    }
}