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
                    createdAt = Instant.fromEpochSeconds(transaction.created_at),
                    items = items,
                )
            }
        }

    fun getTransactionById(id: String): TransactionDataModel? {
        val transaction = database.transactionQueries.selectTransactionById(id).executeAsOneOrNull()
        return transaction?.let {
            val items = getTransactionItemsSync(it.id)
            TransactionDataModel(
                id = it.id,
                createdAt = Instant.fromEpochSeconds(it.created_at),
                items = items,
            )
        }
    }

    fun getTransactionsByDateRange(
        startDate: Instant,
        endDate: Instant,
    ): Flow<List<TransactionDataModel>> = database.transactionQueries
        .selectTransactionsByDateRange(
            startDate.epochSeconds,
            endDate.epochSeconds,
        )
        .asFlow()
        .mapToList(Dispatchers.IO)
        .map { transactions ->
            transactions.map { transaction ->
                val items = getTransactionItemsSync(transaction.id)
                TransactionDataModel(
                    id = transaction.id,
                    createdAt = Instant.fromEpochSeconds(transaction.created_at),
                    items = items,
                )
            }
        }

    fun insertTransaction(
        id: String = UUID.randomUUID().toString(),
        createdAt: Instant,
    ): String {
        database.transactionQueries.insertTransaction(
            id = id,
            created_at = createdAt.epochSeconds,
        )
        return id
    }

    fun addItemToTransaction(
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

    fun removeItemFromTransaction(itemId: Long) {
        database.transactionQueries.deleteTransactionItem(itemId)
    }

    fun deleteTransaction(id: String) {
        database.transactionQueries.deleteTransaction(id)
    }

    fun deleteAllTransactions() {
        database.transactionQueries.deleteAllTransactions()
    }

    fun getTransactionsPaged(
        limit: Long,
        offset: Long,
        startDate: Instant? = null,
        endDate: Instant? = null,
        searchQuery: String? = null,
    ): Flow<List<TransactionDataModel>> = when {
        startDate != null && endDate != null && searchQuery != null -> {
            database.transactionQueries.selectTransactionsPagedWithDateRangeAndSearch(
                startDate.epochSeconds,
                endDate.epochSeconds,
                "%$searchQuery%",
                "%$searchQuery%",
                limit,
                offset,
            )
        }
        startDate != null && endDate != null -> {
            database.transactionQueries.selectTransactionsPagedWithDateRange(
                startDate.epochSeconds,
                endDate.epochSeconds,
                limit,
                offset,
            )
        }
        searchQuery != null -> {
            database.transactionQueries.selectTransactionsPagedWithSearch(
                "%$searchQuery%",
                "%$searchQuery%",
                limit,
                offset,
            )
        }
        else -> {
            database.transactionQueries.selectTransactionsPaged(limit, offset)
        }
    }
        .asFlow()
        .mapToList(Dispatchers.IO)
        .map { transactions ->
            transactions.map { transaction ->
                val items = getTransactionItemsSync(transaction.id)
                TransactionDataModel(
                    id = transaction.id,
                    createdAt = Instant.fromEpochSeconds(transaction.created_at),
                    items = items,
                )
            }
        }

    fun getTransactionCount(
        startDate: Instant? = null,
        endDate: Instant? = null,
        searchQuery: String? = null,
    ): Long = when {
        startDate != null && endDate != null && searchQuery != null -> {
            database.transactionQueries.countTransactionsWithDateRangeAndSearch(
                startDate.epochSeconds,
                endDate.epochSeconds,
                "%$searchQuery%",
                "%$searchQuery%",
            ).executeAsOne()
        }
        startDate != null && endDate != null -> {
            database.transactionQueries.countTransactionsWithDateRange(
                startDate.epochSeconds,
                endDate.epochSeconds,
            ).executeAsOne()
        }
        searchQuery != null -> {
            database.transactionQueries.countTransactionsWithSearch(
                "%$searchQuery%",
                "%$searchQuery%",
            ).executeAsOne()
        }
        else -> {
            database.transactionQueries.countAllTransactions().executeAsOne()
        }
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
