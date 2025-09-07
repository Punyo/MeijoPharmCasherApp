package com.punyo.casherapp.data.transaction.model

data class TransactionItemDataModel(
    val id: Long,
    val quantity: Int,
    val unitPrice: Int,
    val transactionId: String,
    val productId: String?,
    val discountPercent: Double = 0.0,
) {
    val discountAmount: Int
        get() = (unitPrice * discountPercent / 100).toInt()

    val discountedUnitPrice: Int
        get() = unitPrice - discountAmount

    val totalPrice: Int
        get() = discountedUnitPrice * quantity
}
