package com.punyo.casherapp.application.di

import com.punyo.casherapp.ui.product.ProductViewModel
import com.punyo.casherapp.ui.transactions.AllTransactionsSubScreenViewModel
import com.punyo.casherapp.ui.transactions.ProductsListSubScreenViewModel
import com.punyo.casherapp.ui.transactions.TransactionsScreenViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val viewModelModule =
    module {
        viewModel { ProductViewModel(get()) }
        viewModel { AllTransactionsSubScreenViewModel(get(), get()) }
        viewModel { TransactionsScreenViewModel(get(), get()) }
        viewModel { ProductsListSubScreenViewModel(get()) }
    }
