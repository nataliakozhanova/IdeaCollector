package com.example.ideacollector.settings.domain.impl

import com.example.ideacollector.settings.domain.api.SettingsInteractor
import com.example.ideacollector.settings.domain.api.SettingsRepository
import com.example.ideacollector.settings.domain.models.SortType
import kotlinx.coroutines.flow.Flow

class SettingsInteractorImpl(private val settingsRepository: SettingsRepository): SettingsInteractor {
    override fun getSortType(): Flow<SortType?> {
        return settingsRepository.readSortingSettings()
    }

    override suspend fun saveSortType(sortType: SortType) {
        settingsRepository.writeSortingSettings(sortType)
    }
}