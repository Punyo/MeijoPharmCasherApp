package com.punyo.casherapp.application.di

import com.punyo.casherapp.data.product.source.ProductLocalDataSource
import com.punyo.casherapp.data.transaction.source.TransactionLocalDataSource
import org.koin.dsl.module

val sourceModule =
    module {
        single { ProductLocalDataSource(get()) }
        single { TransactionLocalDataSource(get()) }
    }
