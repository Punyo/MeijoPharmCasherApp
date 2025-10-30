package com.punyo.casherapp.application.di

import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.jdbc.sqlite.JdbcSqliteDriver
import com.punyo.casherapp.application.db.AppDatabase
import com.punyo.casherapp.application.util.AppPathUtil
import org.koin.dsl.module
import java.util.Properties

actual val databaseModule =
    module {
        single { AppPathUtil() }
        single<SqlDriver> {
            val appPathUtil: AppPathUtil = get()
            val databasePath = appPathUtil.getDatabasePath()
            val properties = Properties().apply {
                put("foreign_keys", "true")
            }
            JdbcSqliteDriver("jdbc:sqlite:$databasePath", properties).also { driver ->
                AppDatabase.Schema.create(driver)
            }
        }
        single { AppDatabase(get()) }
    }
