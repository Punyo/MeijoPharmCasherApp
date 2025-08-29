package com.punyo.casherapp.data.transaction.model

import kotlinx.datetime.Instant

data class TransactionDataModel(
    val id: String,
    val createdAt: Instant,
    val items: List<TransactionItemDataModel> = emptyList()
) {
    val totalAmount: Int
        get() = items.sumOf { it.totalPrice }

    val totalQuantity: Int
        get() = items.sumOf { it.quantity }
}