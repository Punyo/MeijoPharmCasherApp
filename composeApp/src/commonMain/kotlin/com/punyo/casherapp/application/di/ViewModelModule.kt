package com.punyo.casherapp.application.di

import com.punyo.casherapp.ui.product.ProductViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val viewModelModule =
    module {
        viewModel { ProductViewModel() }
    }
