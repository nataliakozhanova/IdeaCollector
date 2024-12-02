package com.example.ideacollector.settings.domain.api

import com.example.ideacollector.settings.domain.models.SortType
import com.example.ideacollector.settings.domain.models.Theme
import kotlinx.coroutines.flow.Flow

interface SettingsRepository {
    fun readThemeSettings(): Flow<Theme?>
    suspend fun writeThemeSetting(theme: Theme)
    fun readSortingSettings(): Flow<SortType>
    suspend fun writeSortingSettings(sortType: SortType)
    fun readEnablePasswordSettings(): Flow<Boolean>
    suspend fun writeEnablePasswordSettings(isPasswordEnabled: Boolean)
    suspend fun savePassword(password: String)
    fun checkPassword(inputtedPassword: String): Flow<Boolean>
    suspend fun deletePassword()
    fun readIsPasswordSet(): Flow<Boolean>
    suspend fun writeIsPasswordSet(isPasswordSet: Boolean)
}