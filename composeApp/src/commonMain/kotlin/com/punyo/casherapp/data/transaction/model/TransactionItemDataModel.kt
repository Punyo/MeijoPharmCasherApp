package com.punyo.casherapp.data.transaction.model

data class TransactionItemDataModel(
    val id: Long,
    val quantity: Int,
    val unitPrice: Int,
    val transactionId: String,
    val productId: String?,
    val discountAmount: Int = 0,
) {
    val discountedUnitPrice: Int
        get() = unitPrice - discountAmount

    val totalPrice: Int
        get() = discountedUnitPrice * quantity
}
