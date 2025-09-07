package com.punyo.casherapp.data.transaction

import com.punyo.casherapp.data.transaction.model.TransactionDataModel
import com.punyo.casherapp.data.transaction.model.TransactionItemDataModel
import kotlinx.coroutines.flow.Flow
import kotlinx.datetime.Instant

interface TransactionRepository {
    fun getAllTransactions(): Flow<List<TransactionDataModel>>

    suspend fun getTransactionById(id: String): TransactionDataModel?

    fun searchTransactions(
        startDate: Instant? = null,
        endDate: Instant? = null,
        searchQuery: String? = null,
    ): Flow<List<TransactionDataModel>>

    suspend fun getTransactionCount(
        startDate: Instant? = null,
        endDate: Instant? = null,
        searchQuery: String? = null,
    ): Int

    suspend fun insertTransaction(transaction: TransactionDataModel): String

    suspend fun insertTransactionWithItems(
        transactionId: String,
        createdAt: Instant,
        items: List<TransactionItemDataModel>,
    )

    suspend fun addItemToTransaction(
        transactionId: String,
        productId: String?,
        quantity: Int,
        unitPrice: Int,
        discountPercent: Double = 0.0,
    )

    suspend fun removeItemFromTransaction(itemId: Long)

    suspend fun deleteTransaction(id: String)

    suspend fun deleteAllTransactions()
}
