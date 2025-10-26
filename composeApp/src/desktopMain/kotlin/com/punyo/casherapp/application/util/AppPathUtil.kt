package com.punyo.casherapp.application.util

import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths

class AppPathUtil {
    companion object {
        private const val APP_DIR_NAME = ".meijopharmcasherapp"
        private const val DATABASE_FILE_NAME = "database.db"
        private const val SETTINGS_FILE_NAME = "settings.properties"
    }

    private val appDirectory: Path by lazy {
        val homeDir = Paths.get(System.getProperty("user.home"))
        val appDir = homeDir.resolve(APP_DIR_NAME)
        Files.createDirectories(appDir)
        appDir
    }

    fun getAppDirectory(): Path = appDirectory

    fun getDatabasePath(): Path = appDirectory.resolve(DATABASE_FILE_NAME)

    fun getSettingsPath(): Path = appDirectory.resolve(SETTINGS_FILE_NAME)
}
