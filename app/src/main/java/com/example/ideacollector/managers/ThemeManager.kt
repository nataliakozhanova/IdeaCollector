package com.example.ideacollector.managers

import android.content.Context
import android.content.res.Configuration
import androidx.appcompat.app.AppCompatDelegate
import com.example.ideacollector.settings.domain.api.SettingsRepository
import com.example.ideacollector.settings.domain.models.Theme
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch

class ThemeManager(
    private val repository: SettingsRepository,
    private val context: Context
) {

    private val _currentTheme = MutableStateFlow(getSystemTheme())
    val currentTheme: StateFlow<Theme> get() = _currentTheme

    fun setAppTheme(theme: Theme) {
        CoroutineScope(Dispatchers.Main).launch {
        AppCompatDelegate.setDefaultNightMode(
            when (theme) {
                Theme.DARK -> AppCompatDelegate.MODE_NIGHT_YES
                Theme.LIGHT -> AppCompatDelegate.MODE_NIGHT_NO
            }
        )
        }
    }

    private fun getSystemTheme(): Theme {
        val currentNightMode = context.resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK
        return when (currentNightMode) {
            Configuration.UI_MODE_NIGHT_YES -> Theme.DARK
            Configuration.UI_MODE_NIGHT_NO -> Theme.LIGHT
            else -> Theme.LIGHT
        }
    }
    fun applyTheme() {
        CoroutineScope(Dispatchers.Default).launch {
            repository.readThemeSettings()
                .catch {
                    _currentTheme.value = getSystemTheme()
                }
                .collect { savedTheme ->
                    _currentTheme.value = savedTheme ?: getSystemTheme()
                    setAppTheme(_currentTheme.value)
                }
        }
    }

    fun saveTheme(theme: Theme) {
        CoroutineScope(Dispatchers.IO).launch {
            repository.writeThemeSetting(theme)
            _currentTheme.value = theme
        }
    }
}