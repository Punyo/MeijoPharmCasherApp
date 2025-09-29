package com.punyo.casherapp.data.transaction.model

data class TransactionItem(
    val quantity: Int,
    val unitPrice: Int,
    val productId: String?,
    val discountAmount: Int = 0,
)
