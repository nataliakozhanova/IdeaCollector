package com.example.ideacollector.settings.domain.api

import com.example.ideacollector.settings.domain.models.Theme

interface SettingsInteractor {
    fun getThemeSettings(): Theme?
    fun updateThemeSetting(settings: Theme)
}