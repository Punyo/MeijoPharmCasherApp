package com.punyo.casherapp.data.transaction

import com.punyo.casherapp.data.transaction.model.TransactionDataModel
import com.punyo.casherapp.data.transaction.model.TransactionItemDataModel
import com.punyo.casherapp.data.transaction.source.TransactionLocalDataSource
import kotlinx.coroutines.flow.Flow
import kotlinx.datetime.Instant
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

class TransactionRepositoryImpl(
    private val localDataSource: TransactionLocalDataSource,
) : TransactionRepository {
    override fun getAllTransactions(): Flow<List<TransactionDataModel>> = localDataSource.getAllTransactions()

    override suspend fun getTransactionById(id: String): TransactionDataModel? = localDataSource.getTransactionById(id)

    override fun searchTransactions(
        startDate: Instant?,
        endDate: Instant?,
        searchQuery: String?,
    ): Flow<List<TransactionDataModel>> = localDataSource.searchTransactions(
        startDate = startDate,
        endDate = endDate,
        searchQuery = searchQuery,
    )

    @OptIn(ExperimentalUuidApi::class)
    override suspend fun insertTransactionWithItems(
        createdAt: Instant,
        items: List<TransactionItemDataModel>,
    ) {
        val uuid = Uuid.random().toString()
        localDataSource.insertTransaction(uuid, createdAt)
        items.forEach { item ->
            localDataSource.addItemToTransaction(
                transactionId = uuid,
                productId = item.productId,
                quantity = item.quantity,
                unitPrice = item.unitPrice,
                discountPercent = item.discountPercent,
            )
        }
    }

    override suspend fun removeItemFromTransaction(itemId: Long) {
        localDataSource.removeItemFromTransaction(itemId)
    }

    override suspend fun deleteTransaction(id: String) {
        localDataSource.deleteTransaction(id)
    }

    override suspend fun deleteAllTransactions() {
        localDataSource.deleteAllTransactions()
    }

    override suspend fun getTransactionCount(
        startDate: Instant?,
        endDate: Instant?,
        searchQuery: String?,
    ): Int = localDataSource.getTransactionCount(
        startDate = startDate,
        endDate = endDate,
        searchQuery = searchQuery,
    ).toInt()
}
