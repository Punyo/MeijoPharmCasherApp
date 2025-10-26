package com.punyo.casherapp.application.di

import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.jdbc.sqlite.JdbcSqliteDriver
import com.punyo.casherapp.application.db.AppDatabase
import com.punyo.casherapp.application.util.AppPathUtil
import org.koin.dsl.module

actual val databaseModule =
    module {
        single { AppPathUtil() }
        single<SqlDriver> {
            val appPathUtil: AppPathUtil = get()
            val databasePath = appPathUtil.getDatabasePath()
            JdbcSqliteDriver("jdbc:sqlite:$databasePath").also {
                AppDatabase.Schema.create(it)
            }
        }
        single { AppDatabase(get()) }
    }
