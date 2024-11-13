package com.example.ideacollector.settings.data.storage

import com.example.ideacollector.settings.domain.models.Theme

interface SettingsStorageApi {
    fun readThemeSettings() : Theme?
    fun writeThemeSettings(themeSettings: Theme)
}