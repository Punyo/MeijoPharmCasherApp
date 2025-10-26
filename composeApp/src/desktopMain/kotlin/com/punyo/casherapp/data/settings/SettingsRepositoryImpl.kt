package com.punyo.casherapp.data.settings

import com.punyo.casherapp.application.util.AppPathUtil
import com.punyo.casherapp.ui.theme.ThemeMode
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.io.File
import java.nio.file.Files
import java.util.Properties

class SettingsRepositoryImpl(
    appPathUtil: AppPathUtil,
) : SettingsRepository {
    private val settingsFile: File = appPathUtil.getSettingsPath().toFile().also {
        if (!it.exists()) {
            Files.createFile(it.toPath())
        }
    }
    private val themeModeFlow = MutableStateFlow(loadThemeMode())

    private fun loadThemeMode(): ThemeMode {
        return try {
            if (settingsFile.exists()) {
                val properties = Properties()
                settingsFile.inputStream().use { properties.load(it) }
                val themeName = properties.getProperty(KEY_THEME_MODE, ThemeMode.LIGHT.name)
                ThemeMode.valueOf(themeName)
            } else {
                ThemeMode.LIGHT
            }
        } catch (e: Exception) {
            ThemeMode.LIGHT
        }
    }

    override fun getThemeMode(): Flow<ThemeMode> = themeModeFlow.asStateFlow()

    override suspend fun setThemeMode(mode: ThemeMode) {
        try {
            val properties = Properties()
            if (settingsFile.exists()) {
                settingsFile.inputStream().use { properties.load(it) }
            }
            properties.setProperty(KEY_THEME_MODE, mode.name)
            settingsFile.outputStream().use { properties.store(it, "App Settings") }
            themeModeFlow.value = mode
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    companion object {
        private const val KEY_THEME_MODE = "theme_mode"
    }
}
