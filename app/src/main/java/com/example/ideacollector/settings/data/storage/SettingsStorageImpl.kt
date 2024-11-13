package com.example.ideacollector.settings.data.storage

import android.content.SharedPreferences
import com.example.ideacollector.settings.domain.models.Theme

const val THEME_KEY = "key_for_app_theme"

class SettingsStorageImpl(
    private val sharedPreferences: SharedPreferences,
) : SettingsStorageApi {
    override fun readThemeSettings(): Theme? {
        val theme = sharedPreferences.getString(THEME_KEY, null)
        if (theme != null) return enumValueOf<Theme>(theme)
        return null
    }

    override fun writeThemeSettings(themeSettings: Theme) {
        val theme = themeSettings.toString()
        sharedPreferences.edit()
            .putString(THEME_KEY, theme)
            .apply()
    }
}