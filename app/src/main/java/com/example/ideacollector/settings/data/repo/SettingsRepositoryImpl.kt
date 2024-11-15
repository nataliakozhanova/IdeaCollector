package com.example.ideacollector.settings.data.repo

import com.example.ideacollector.settings.data.storage.SettingsStorageApi
import com.example.ideacollector.settings.domain.api.SettingsRepository
import com.example.ideacollector.settings.domain.models.SortType
import com.example.ideacollector.settings.domain.models.Theme
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf

class SettingsRepositoryImpl(private val storageApi : SettingsStorageApi) : SettingsRepository {
    override fun getThemeSettings(): Theme? {
        return storageApi.readThemeSettings()
    }

    override fun updateThemeSetting(settings: Theme) {
        storageApi.writeThemeSettings(settings)
    }

    override fun getSortType(): Flow<SortType> {
        // TODO - сделать получение из датастор
        return flowOf(SortType.PRIORITY)
    }
}