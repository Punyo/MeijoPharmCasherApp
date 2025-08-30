package com.punyo.casherapp.ui.transactions

import androidx.lifecycle.ViewModel
import com.punyo.casherapp.data.transaction.TransactionRepository

class ProductsListSubScreenViewModel(
    private val transactionRepository: TransactionRepository,
) : ViewModel()
