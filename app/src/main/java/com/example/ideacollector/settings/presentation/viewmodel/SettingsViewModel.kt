package com.example.ideacollector.settings.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ideacollector.managers.ThemeManager
import com.example.ideacollector.settings.domain.api.SettingsInteractor
import com.example.ideacollector.settings.domain.models.EnablePassword
import com.example.ideacollector.settings.domain.models.SortType
import com.example.ideacollector.settings.domain.models.Theme
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch

class SettingsViewModel(
    private val settingsInteractor: SettingsInteractor,
    private val themeManager: ThemeManager,
) : ViewModel() {
    private val _currentThemeSettings = MutableStateFlow(themeManager.currentTheme.value)
    val currentThemeSettings: StateFlow<Theme> get() = _currentThemeSettings

    private val _currentSortingSettings = MutableStateFlow(SortType.DATE)
    val currentSortingSettings: StateFlow<SortType> get() = _currentSortingSettings

    private val _isPasswordEnabled = MutableStateFlow(EnablePassword(isPasswordEnabled = false))
    val isPasswordEnabled: StateFlow<EnablePassword> get() = _isPasswordEnabled

    fun getCheckboxIsPasswordEnabled() {
        CoroutineScope(Dispatchers.Default).launch {
            settingsInteractor.getEnablePassword()
                .catch {
                    _isPasswordEnabled.value = EnablePassword(isPasswordEnabled = false)
                }
                .collect { isEnabled ->
                    _isPasswordEnabled.value = isEnabled
                }
        }
    }

    fun changeCheckboxIsPasswordEnabled(isEnabled: Boolean) {
        viewModelScope.launch {
            settingsInteractor.saveEnablePassword(isEnabled)
            _isPasswordEnabled.value = EnablePassword(isEnabled)
        }
    }

    fun getSortType() {
        CoroutineScope(Dispatchers.Default).launch {
            settingsInteractor.getSortType()
                .catch {
                    _currentSortingSettings.value = SortType.DATE
                }
                .collect { savedSortType ->
                    _currentSortingSettings.value = savedSortType
                }
        }
    }

    fun changeSortType() {
        _currentSortingSettings.value = when (_currentSortingSettings.value) {
            SortType.PRIORITY -> SortType.DATE
            SortType.DATE -> SortType.PRIORITY
        }
        viewModelScope.launch(Dispatchers.IO) {
            settingsInteractor.saveSortType(_currentSortingSettings.value)
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