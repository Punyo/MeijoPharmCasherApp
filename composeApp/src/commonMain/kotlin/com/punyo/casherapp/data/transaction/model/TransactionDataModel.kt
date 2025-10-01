package com.punyo.casherapp.data.transaction.model

import kotlinx.datetime.Instant
import org.joda.money.Money

data class TransactionDataModel(
    val id: String,
    val createdAt: Instant,
    val items: List<TransactionItemDataModel> = emptyList(),
) {
    val totalAmount: Money
        get() = items.fold(items.firstOrNull()?.totalPrice?.currencyUnit?.let { Money.zero(it) } 
            ?: com.punyo.casherapp.extensions.JPY.let { Money.zero(it) }) { acc, item -> 
            acc.plus(item.totalPrice) 
        }

    val totalQuantity: Int
        get() = items.sumOf { it.quantity }
}
