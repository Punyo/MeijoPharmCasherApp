package com.punyo.casherapp.data.transaction.model

import org.joda.money.Money

data class TransactionItemDataModel(
    val id: Long,
    val quantity: Int,
    val unitPrice: Money,
    val transactionId: String,
    val productId: String?,
    val discountAmount: Money,
) {
    val discountedUnitPrice: Money
        get() = unitPrice.minus(discountAmount)

    val totalPrice: Money
        get() = discountedUnitPrice.multipliedBy(quantity.toLong())
}
