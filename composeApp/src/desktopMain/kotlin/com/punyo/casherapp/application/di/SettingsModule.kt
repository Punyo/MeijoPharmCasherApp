package com.punyo.casherapp.application.di

import com.punyo.casherapp.data.settings.SettingsRepository
import com.punyo.casherapp.data.settings.SettingsRepositoryImpl
import org.koin.dsl.module

val settingsModule =
    module {
        single<SettingsRepository> { SettingsRepositoryImpl(get()) }
    }
