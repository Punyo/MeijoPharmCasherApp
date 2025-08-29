package com.punyo.casherapp.data.transaction.source

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import com.punyo.casherapp.application.db.AppDatabase
import com.punyo.casherapp.data.transaction.model.TransactionDataModel
import com.punyo.casherapp.data.transaction.model.TransactionItemDataModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.datetime.Instant
import java.util.UUID

class TransactionLocalDataSource(
    private val database: AppDatabase,
) {
    fun getAllTransactions(): Flow<List<TransactionDataModel>> = database.transactionQueries
        .selectAllTransactions()
        .asFlow()
        .mapToList(Dispatchers.IO)
        .map { transactions ->
            transactions.map { transaction ->
                val items = getTransactionItemsSync(transaction.id)
                TransactionDataModel(
                    id = transaction.id,
                    createdAt = Instant.fromEpochMilliseconds(transaction.created_at),
                    items = items,
                )
            }
        }

    suspend fun getTransactionById(id: String): TransactionDataModel? {
        val transaction = database.transactionQueries.selectTransactionById(id).executeAsOneOrNull()
        return transaction?.let {
            val items = getTransactionItemsSync(it.id)
            TransactionDataModel(
                id = it.id,
                createdAt = Instant.fromEpochMilliseconds(it.created_at),
                items = items,
            )
        }
    }

    fun getTransactionsByDateRange(
        startDate: Instant,
        endDate: Instant,
    ): Flow<List<TransactionDataModel>> = database.transactionQueries
        .selectTransactionsByDateRange(
            startDate.toEpochMilliseconds(),
            endDate.toEpochMilliseconds(),
        )
        .asFlow()
        .mapToList(Dispatchers.IO)
        .map { transactions ->
            transactions.map { transaction ->
                val items = getTransactionItemsSync(transaction.id)
                TransactionDataModel(
                    id = transaction.id,
                    createdAt = Instant.fromEpochMilliseconds(transaction.created_at),
                    items = items,
                )
            }
        }

    suspend fun insertTransaction(
        id: String = UUID.randomUUID().toString(),
        createdAt: Instant,
    ): String {
        database.transactionQueries.insertTransaction(
            id = id,
            created_at = createdAt.toEpochMilliseconds(),
        )
        return id
    }

    suspend fun addItemToTransaction(
        transactionId: String,
        productId: String?,
        quantity: Int,
        unitPrice: Int,
        discountPercent: Double = 0.0,
    ) {
        database.transactionQueries.insertTransactionItem(
            quantity = quantity.toLong(),
            unit_price = unitPrice.toLong(),
            transaction_id = transactionId,
            product_id = productId,
            discount_percent = discountPercent,
        )
    }

    suspend fun removeItemFromTransaction(itemId: Long) {
        database.transactionQueries.deleteTransactionItem(itemId)
    }

    suspend fun deleteTransaction(id: String) {
        database.transactionQueries.deleteTransaction(id)
    }

    suspend fun deleteAllTransactions() {
        database.transactionQueries.deleteAllTransactions()
    }

    private fun getTransactionItemsSync(transactionId: String): List<TransactionItemDataModel> = database.transactionQueries.selectTransactionItems(transactionId).executeAsList().map { item ->
        TransactionItemDataModel(
            id = item.id,
            quantity = item.quantity.toInt(),
            unitPrice = item.unit_price.toInt(),
            transactionId = item.transaction_id,
            productId = item.product_id,
            discountPercent = item.discount_percent,
        )
    }
}
