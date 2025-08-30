package com.punyo.casherapp.application.di

import com.punyo.casherapp.ui.product.ProductViewModel
import com.punyo.casherapp.ui.transactions.AllTransactionsScreenViewModel
import com.punyo.casherapp.ui.transactions.ProductsListScreenViewModel
import com.punyo.casherapp.ui.transactions.TransactionsScreenViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val viewModelModule =
    module {
        viewModel { ProductViewModel(get()) }
        viewModel { AllTransactionsScreenViewModel(get(), get()) }
        viewModel { TransactionsScreenViewModel(get(), get()) }
        viewModel { ProductsListScreenViewModel(get()) }
    }
