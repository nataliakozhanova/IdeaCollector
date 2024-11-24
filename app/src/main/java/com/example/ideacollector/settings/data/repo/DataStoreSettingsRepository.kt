package com.example.ideacollector.settings.data.repo

import androidx.datastore.core.IOException
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.stringPreferencesKey
import com.example.ideacollector.settings.data.repo.DataStoreSettingsRepository.PreferencesKeys.THEME_KEY
import com.example.ideacollector.settings.domain.api.SettingsRepository
import com.example.ideacollector.settings.domain.models.Theme
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map

class DataStoreSettingsRepository(private val dataStore: androidx.datastore.core.DataStore<Preferences>) :
    SettingsRepository {

    private object PreferencesKeys {
        val THEME_KEY = stringPreferencesKey("theme_key")
    }

    override fun readThemeSettings(): Flow<Theme?> {
        val theme: Flow<Theme?> = dataStore.data
            .catch {exception ->
                if (exception is IOException) {
                    emit(emptyPreferences())
                } else {
                    throw exception
                }
            }.map { preferences ->
                val themeName = preferences[THEME_KEY]
                themeName?.let { Theme.valueOf(it) }
            }
        return theme
    }

    override suspend fun writeThemeSetting(theme: Theme) {
        dataStore.edit { preferences ->
            preferences[THEME_KEY] = theme.name
        }
    }

}