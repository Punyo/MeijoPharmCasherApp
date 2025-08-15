package com.punyo.casherapp.application.di

import com.punyo.casherapp.data.product.source.ProductLocalDataSource
import org.koin.dsl.module

val sourceModule =
    module {
        single { ProductLocalDataSource(get()) }
    }
