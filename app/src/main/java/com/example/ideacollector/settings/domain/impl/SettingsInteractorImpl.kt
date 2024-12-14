package com.example.ideacollector.settings.domain.impl

import com.example.ideacollector.settings.domain.api.SettingsInteractor
import com.example.ideacollector.settings.domain.api.SettingsRepository
import com.example.ideacollector.settings.domain.models.SortType
import kotlinx.coroutines.flow.Flow

class SettingsInteractorImpl(private val settingsRepository: SettingsRepository) :
    SettingsInteractor {
    override fun getSortType(): Flow<SortType> {
        return settingsRepository.readSortingSettings()
    }

    override suspend fun saveSortType(sortType: SortType) {
        settingsRepository.writeSortingSettings(sortType)
    }

    override fun getEnablePassword(): Flow<Boolean> {
        return settingsRepository.readEnablePasswordSettings()
    }

    override suspend fun saveEnablePassword(isPasswordEnabled: Boolean) {
        settingsRepository.writeEnablePasswordSettings(isPasswordEnabled)
    }

    override suspend fun setPassword(password: String) {
        settingsRepository.savePassword(password)
    }

    override suspend fun deletePassword() {
        settingsRepository.deletePassword()
    }

    override fun getIsPasswordSet(): Flow<Boolean> {
        return settingsRepository.readIsPasswordSet()
    }

    override suspend fun saveIsPasswordSet(isPasswordSet: Boolean) {
        settingsRepository.writeIsPasswordSet(isPasswordSet)
    }

}