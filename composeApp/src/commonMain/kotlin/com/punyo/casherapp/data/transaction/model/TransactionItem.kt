package com.punyo.casherapp.data.transaction.model

import org.joda.money.Money

data class TransactionItem(
    val quantity: Int,
    val unitPrice: Money,
    val productId: String?,
    val discountAmount: Money,
)
