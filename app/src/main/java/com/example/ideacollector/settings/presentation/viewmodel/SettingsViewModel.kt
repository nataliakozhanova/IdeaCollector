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

    val isPasswordEnabled = settingsInteractor.getEnablePassword().stateIn(
        viewModelScope,
        SharingStarted.Lazily,
        false
    )

    fun changeCheckboxIsPasswordEnabled(isEnabled: Boolean) {
        viewModelScope.launch {
            settingsInteractor.saveEnablePassword(isEnabled)
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
        _currentThemeSettings.value = when (_currentThemeSettings.value) {
            Theme.LIGHT -> Theme.DARK
            Theme.DARK -> Theme.LIGHT
        }
        viewModelScope.launch(Dispatchers.IO) {
            themeManager.saveTheme(_currentThemeSettings.value)
        }
    }

}