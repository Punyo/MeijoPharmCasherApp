package com.punyo.casherapp.data.transaction.source

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import com.punyo.casherapp.application.db.AppDatabase
import com.punyo.casherapp.data.transaction.db.Transactions
import com.punyo.casherapp.data.transaction.model.TransactionDataModel
import com.punyo.casherapp.data.transaction.model.TransactionItem
import com.punyo.casherapp.data.transaction.model.TransactionItemDataModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import kotlinx.datetime.Instant
import java.util.UUID
import kotlin.collections.map

class TransactionLocalDataSource(
    private val database: AppDatabase,
) {
    fun getAllTransactions(): Flow<List<TransactionDataModel>> = database.transactionQueries
        .selectAllTransactions()
        .asFlow()
        .mapToList(Dispatchers.IO)
        .map { transactions ->
            mapTransactionsToTransactionDataModels(transactions)
        }

    suspend fun mapTransactionsToTransactionDataModels(transactions: List<Transactions>): List<TransactionDataModel> = withContext(Dispatchers.IO) {
        transactions.map { transaction ->
            async(Dispatchers.IO) {
                val items = getTransactionItems(transaction.id)
                TransactionDataModel(
                    id = transaction.id,
                    createdAt = Instant.fromEpochSeconds(transaction.created_at),
                    items = items,
                )
            }
        }.awaitAll()
    }

    suspend fun getTransactionById(id: String): TransactionDataModel? {
        val transaction = database.transactionQueries.selectTransactionById(id).executeAsOneOrNull()
        return withContext(Dispatchers.IO) {
            transaction?.let {
                val items = getTransactionItems(it.id)
                TransactionDataModel(
                    id = it.id,
                    createdAt = Instant.fromEpochSeconds(it.created_at),
                    items = items,
                )
            }
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
        item: TransactionItem,
    ) {
        database.transactionQueries.insertTransactionItem(
            quantity = item.quantity.toLong(),
            unit_price = item.unitPrice.toLong(),
            transaction_id = transactionId,
            product_id = item.productId,
            discount_amount = item.discountAmount.toLong(),
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

    fun searchTransactions(
        startDate: Instant? = null,
        endDate: Instant? = null,
        searchQuery: String? = null,
    ): Flow<List<TransactionDataModel>> = when {
        startDate != null && endDate != null && searchQuery != null -> {
            database.transactionQueries.selectTransactionsWithDateRangeAndSearch(
                startDate.epochSeconds,
                endDate.epochSeconds,
                "%$searchQuery%",
                "%$searchQuery%",
            )
        }

        startDate != null && endDate != null -> {
            database.transactionQueries.selectTransactionsByDateRange(
                startDate.epochSeconds,
                endDate.epochSeconds,
            )
        }

        searchQuery != null -> {
            database.transactionQueries.selectTransactionsWithSearch(
                "%$searchQuery%",
                "%$searchQuery%",
            )
        }

        else -> {
            database.transactionQueries.selectAllTransactions()
        }
    }
        .asFlow()
        .mapToList(Dispatchers.IO)
        .map { transactions ->
            mapTransactionsToTransactionDataModels(transactions)
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

    private fun getTransactionItems(transactionId: String): List<TransactionItemDataModel> = database.transactionQueries.selectTransactionItems(transactionId)
        .executeAsList()
        .map { item ->
            TransactionItemDataModel(
                id = item.id,
                quantity = item.quantity.toInt(),
                unitPrice = item.unit_price.toInt(),
                transactionId = item.transaction_id,
                productId = item.product_id,
                discountAmount = item.discount_amount.toInt(),
            )
        }
}
