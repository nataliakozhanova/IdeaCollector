package com.example.ideacollector.settings.data.repo

import androidx.datastore.core.IOException
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.stringPreferencesKey
import com.example.ideacollector.settings.data.repo.DataStoreSettingsRepository.PreferencesKeys.ENABLE_PASSWORD_KEY
import com.example.ideacollector.settings.data.repo.DataStoreSettingsRepository.PreferencesKeys.IS_PASSWORD_SET_KEY
import com.example.ideacollector.settings.data.repo.DataStoreSettingsRepository.PreferencesKeys.PASSWORD_IV_KEY
import com.example.ideacollector.settings.data.repo.DataStoreSettingsRepository.PreferencesKeys.PASSWORD_KEY
import com.example.ideacollector.settings.data.repo.DataStoreSettingsRepository.PreferencesKeys.SORTING_KEY
import com.example.ideacollector.settings.data.repo.DataStoreSettingsRepository.PreferencesKeys.THEME_KEY
import com.example.ideacollector.settings.domain.api.SettingsRepository
import com.example.ideacollector.settings.domain.models.SortType
import com.example.ideacollector.settings.domain.models.Theme
import com.example.ideacollector.util.CryptoUtils
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map

class DataStoreSettingsRepository(private val dataStore: androidx.datastore.core.DataStore<Preferences>) :
    SettingsRepository {
    private object PreferencesKeys {
        val THEME_KEY = stringPreferencesKey("theme_key")
        val SORTING_KEY = stringPreferencesKey("sorting_key")
        val ENABLE_PASSWORD_KEY = booleanPreferencesKey("enable_password_key")
        val PASSWORD_KEY = stringPreferencesKey("password")
        val PASSWORD_IV_KEY = stringPreferencesKey("password_iv")
        val IS_PASSWORD_SET_KEY = booleanPreferencesKey("is_password_set_key")
    }

    override fun readThemeSettings(): Flow<Theme?> {
        val theme: Flow<Theme?> = dataStore.data
            .catch { exception ->
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

    override fun readSortingSettings(): Flow<SortType> {
        return dataStore.data
            .catch { exception ->
                if (exception is IOException) {
                    emit(emptyPreferences())
                } else {
                    throw exception
                }
            }
            .map { preferences ->
                val sortType = preferences[SORTING_KEY]
                sortType?.let { SortType.valueOf(it) } ?: SortType.DATE
            }
    }

    override suspend fun writeSortingSettings(sortType: SortType) {
        dataStore.edit { preferences ->
            preferences[SORTING_KEY] = sortType.name
        }
    }

    override fun readEnablePasswordSettings(): Flow<Boolean> {
        return dataStore.data
            .catch { exception ->
                exception.printStackTrace() // Логируем все ошибки
                emit(emptyPreferences()) // Эмитим пустые настройки в случае ошибки
            }
            .map { preferences ->
                preferences[ENABLE_PASSWORD_KEY] ?: false // Если ключ отсутствует, возвращаем false
            }
    }

    override suspend fun writeEnablePasswordSettings(isPasswordEnabled: Boolean) {
        dataStore.edit { preferences ->
            preferences[ENABLE_PASSWORD_KEY] = isPasswordEnabled
        }
    }

    override suspend fun savePassword(password: String) {
        val (iv, encryptedData) = CryptoUtils.encrypt(password)
        dataStore.edit { preferences ->
            preferences[PASSWORD_KEY] = encryptedData.toBase64()
            preferences[PASSWORD_IV_KEY] = iv.toBase64()
        }
    }

    override fun checkPassword(inputtedPassword: String): Flow<Boolean> {
        return flow {
            try {
                val preferences = dataStore.data.first()
                val encryptedPassword = preferences[PASSWORD_KEY]?.fromBase64()
                val iv = preferences[PASSWORD_IV_KEY]?.fromBase64()

                val isPasswordCorrect = if (encryptedPassword != null && iv != null) {
                    try {
                        val savedPassword = CryptoUtils.decrypt(iv, encryptedPassword)
                        if (savedPassword.isEmpty()) {
                            inputtedPassword.isEmpty()
                        } else {
                            savedPassword == inputtedPassword
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                        false
                    }
                } else {
                    inputtedPassword.isEmpty()
                }
                emit(isPasswordCorrect) // Эмитим результат проверки
            } catch (e: Exception) {
                e.printStackTrace()
                emit(false) // Эмитим false в случае ошибки
            }
        }
    }


    override suspend fun deletePassword() {
        dataStore.edit { preferences ->
            // Удаляем только если ключи существуют
            if (preferences.contains(PASSWORD_KEY)) {
                preferences.remove(PASSWORD_KEY)
            }
            if (preferences.contains(PASSWORD_IV_KEY)) {
                preferences.remove(PASSWORD_IV_KEY)
            }
        }
    }

    override fun readIsPasswordSet(): Flow<Boolean> {
        return dataStore.data
            .catch { exception ->
                exception.printStackTrace() // Логируем все ошибки
                emit(emptyPreferences()) // Эмитим пустые настройки в случае ошибки
            }
            .map { preferences ->
                preferences[IS_PASSWORD_SET_KEY] ?: false // Если ключ отсутствует, возвращаем false
            }
    }

    override suspend fun writeIsPasswordSet(isPasswordSet: Boolean) {
        dataStore.edit { preferences ->
            preferences[IS_PASSWORD_SET_KEY] = isPasswordSet
        }
    }

    private fun ByteArray.toBase64(): String =
        android.util.Base64.encodeToString(this, android.util.Base64.DEFAULT)

    private fun String.fromBase64(): ByteArray =
        android.util.Base64.decode(this, android.util.Base64.DEFAULT)
}