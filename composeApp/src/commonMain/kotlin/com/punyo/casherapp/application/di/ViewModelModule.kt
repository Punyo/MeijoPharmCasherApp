package com.punyo.casherapp.application.di

import com.punyo.casherapp.ui.product.ProductScreenViewModel
import com.punyo.casherapp.ui.register.RegisterScreenViewModel
import com.punyo.casherapp.ui.transactions.TransactionsScreenViewModel
import com.punyo.casherapp.ui.transactions.alltransactions.AllTransactionsSubScreenViewModel
import com.punyo.casherapp.ui.transactions.productlist.ProductsListSubScreenViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val viewModelModule =
    module {
        viewModel { ProductScreenViewModel(get()) }
        viewModel { AllTransactionsSubScreenViewModel(get()) }
        viewModel { TransactionsScreenViewModel(get(), get()) }
        viewModel { ProductsListSubScreenViewModel(get(), get()) }
        viewModel { RegisterScreenViewModel(get(), get()) }
    }
