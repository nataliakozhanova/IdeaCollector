package com.example.ideacollector.settings.presentation.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.example.ideacollector.di.App
import com.example.ideacollector.settings.domain.api.SettingsInteractor
import com.example.ideacollector.settings.domain.models.Theme
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class SettingsViewModel(
    application: Application,
    private val settingsInteractor: SettingsInteractor,
) : AndroidViewModel(application) {

    private val _currentTheme = MutableStateFlow((application as App).getCurrentTheme())
    val currentTheme: StateFlow<Theme> get() = _currentTheme

    fun changeTheme() {
        _currentTheme.value = when (_currentTheme.value) {
            Theme.LIGHT -> Theme.DARK
            Theme.DARK -> Theme.LIGHT
        }
        (getApplication<Application>() as App).switchTheme(_currentTheme.value)
        settingsInteractor.updateThemeSetting(_currentTheme.value)
    }
}