package com.example.ideacollector.settings.domain.api

import com.example.ideacollector.settings.domain.models.SortType
import kotlinx.coroutines.flow.Flow

interface SettingsInteractor {
    fun getSortType(): Flow<SortType?>
    suspend fun saveSortType(sortType: SortType)
}