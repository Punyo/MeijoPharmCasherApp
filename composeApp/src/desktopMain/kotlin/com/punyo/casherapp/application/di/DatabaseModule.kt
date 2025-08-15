package com.punyo.casherapp.application.di

import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.jdbc.sqlite.JdbcSqliteDriver
import com.punyo.casherapp.application.db.AppDatabase
import org.koin.dsl.module
import java.io.File
import java.nio.file.FileSystems

actual val databaseModule =
    module {
        single<SqlDriver> {
            val separator = FileSystems.getDefault().separator
            val appDir = "${System.getProperty("user.home")}$separator.meijopharmcasherapp"
            File(appDir).mkdirs()
            JdbcSqliteDriver("jdbc:sqlite:${appDir}${separator}database.db").also {
                AppDatabase.Schema.create(it)
            }
        }
        single { AppDatabase(get()) }
    }
