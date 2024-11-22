package com.example.ideacollector.settings.data.repo

import com.example.ideacollector.settings.data.storage.SettingsStorageApi
import com.example.ideacollector.settings.domain.api.SettingsRepository
import com.example.ideacollector.settings.domain.models.Theme

class SettingsRepositoryImpl(private val storageApi : SettingsStorageApi) : SettingsRepository {
    override fun getThemeSettings(): Theme? {
        return storageApi.readThemeSettings()
    }

    override fun updateThemeSetting(settings: Theme) {
        storageApi.writeThemeSettings(settings)
    }
}