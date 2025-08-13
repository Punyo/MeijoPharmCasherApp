package com.punyo.casherapp.application.di

import org.koin.dsl.module

val appModule = module {
    includes(
        viewModelModule,
        repositoryModule
    )
}