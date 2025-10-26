package com.punyo.casherapp.data.settings

import com.punyo.casherapp.ui.theme.ThemeMode
import kotlinx.coroutines.flow.Flow

interface SettingsRepository {
    fun getThemeMode(): Flow<ThemeMode>

    suspend fun setThemeMode(mode: ThemeMode)
}
