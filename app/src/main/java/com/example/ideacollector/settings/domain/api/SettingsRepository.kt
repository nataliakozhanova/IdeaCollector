package com.example.ideacollector.settings.domain.api

import com.example.ideacollector.settings.domain.models.SortType
import com.example.ideacollector.settings.domain.models.Theme
import kotlinx.coroutines.flow.Flow

interface SettingsRepository {
    fun getThemeSettings(): Theme?
    fun updateThemeSetting(settings: Theme)
    fun getSortType(): Flow<SortType>
}