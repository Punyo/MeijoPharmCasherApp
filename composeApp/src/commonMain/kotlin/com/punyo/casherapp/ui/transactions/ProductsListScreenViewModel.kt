package com.punyo.casherapp.ui.transactions

import androidx.lifecycle.ViewModel
import com.punyo.casherapp.data.transaction.TransactionRepository

class ProductsListScreenViewModel(
    private val transactionRepository: TransactionRepository,
) : ViewModel()
