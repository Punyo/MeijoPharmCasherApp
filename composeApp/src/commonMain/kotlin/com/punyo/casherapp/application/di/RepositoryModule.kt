package com.punyo.casherapp.application.di

import com.punyo.casherapp.data.product.ProductRepository
import com.punyo.casherapp.data.product.ProductRepositoryImpl
import com.punyo.casherapp.data.transaction.TransactionRepository
import com.punyo.casherapp.data.transaction.TransactionRepositoryImpl
import org.koin.dsl.module

val repositoryModule =
    module {
        single<ProductRepository> { ProductRepositoryImpl(get()) }
        single<TransactionRepository> { TransactionRepositoryImpl(get()) }
    }
