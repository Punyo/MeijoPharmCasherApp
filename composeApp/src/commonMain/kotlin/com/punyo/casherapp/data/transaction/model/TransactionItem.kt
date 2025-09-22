package com.punyo.casherapp.data.transaction.model

data class TransactionItem(
    val quantity: Int,
    val unitPrice: Int,
    val productId: String?,
    val discountPercent: Double = 0.0,
)
