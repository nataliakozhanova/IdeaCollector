package com.example.ideacollector.settings.domain.impl

import com.example.ideacollector.settings.domain.api.SettingsInteractor
import com.example.ideacollector.settings.domain.api.SettingsRepository
import com.example.ideacollector.settings.domain.models.Theme

class SettingsInteractorImpl(private val repository: SettingsRepository): SettingsInteractor {
    override fun getThemeSettings(): Theme? {
        return repository.getThemeSettings()
    }

    override fun updateThemeSetting(settings: Theme) {
        repository.updateThemeSetting(settings)
    }
}