package com.example.ideacollector.settings.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ideacollector.managers.ThemeManager
import com.example.ideacollector.settings.domain.api.SettingsInteractor
import com.example.ideacollector.settings.domain.models.SortType
import com.example.ideacollector.settings.domain.models.Theme
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class SettingsViewModel(
    private val settingsInteractor: SettingsInteractor,
    private val themeManager: ThemeManager,
) : ViewModel() {
    private val _currentThemeSettings = MutableStateFlow(themeManager.currentTheme.value)
    val currentThemeSettings: StateFlow<Theme> get() = _currentThemeSettings

    val isPasswordSet: StateFlow<Boolean> get() = settingsInteractor.getIsPasswordSet().stateIn(
        viewModelScope,
        SharingStarted.Lazily,
        false
    )

    val isPasswordEnabled = settingsInteractor.getEnablePassword().stateIn(
        viewModelScope,
        SharingStarted.Lazily,
        false
    )

    fun changeCheckboxIsPasswordEnabled(isEnabled: Boolean) {
        viewModelScope.launch {
            if (!isEnabled) {
                // Удаляем пароль при отключении чекбокса
                settingsInteractor.deletePassword()
                // Сохраняем состояние настройки isPasswordSet
                settingsInteractor.saveIsPasswordSet(false)
            }
            // Сохраняем состояние чекбокса
            settingsInteractor.saveEnablePassword(isEnabled)
        }
    }

    fun setPassword(password: String) {
        viewModelScope.launch {
            settingsInteractor.setPassword(password)
            settingsInteractor.saveIsPasswordSet(true)
        }
    }

    val sortTypeState = settingsInteractor.getSortType().stateIn(
        viewModelScope,
        SharingStarted.Lazily,
        SortType.DATE
    )

    fun changeSortType() {
        viewModelScope.launch {
            val newSortType = when (sortTypeState.value) {
                SortType.PRIORITY -> SortType.DATE
                SortType.DATE -> SortType.PRIORITY
            }
            settingsInteractor.saveSortType(newSortType)
        }
    }

    fun changeTheme() {
        viewModelScope.launch(Dispatchers.IO) {
            _currentThemeSettings.value = when (_currentThemeSettings.value) {
                Theme.LIGHT -> Theme.DARK
                Theme.DARK -> Theme.LIGHT
            }
            themeManager.saveTheme(_currentThemeSettings.value)
        }
    }

}