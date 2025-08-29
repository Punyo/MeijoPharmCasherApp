package com.punyo.casherapp.data.transaction

import com.punyo.casherapp.data.transaction.model.TransactionDataModel
import com.punyo.casherapp.data.transaction.model.TransactionItemDataModel
import com.punyo.casherapp.data.transaction.source.TransactionLocalDataSource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.datetime.Instant

class TransactionRepositoryImpl(
    private val localDataSource: TransactionLocalDataSource,
) : TransactionRepository {
    override fun getAllTransactions(): Flow<List<TransactionDataModel>> = localDataSource.getAllTransactions()

    override suspend fun getTransactionById(id: String): TransactionDataModel? = localDataSource.getTransactionById(id)

    override fun getTransactionsByDateRange(
        startDate: Instant,
        endDate: Instant,
    ): Flow<List<TransactionDataModel>> = localDataSource.getTransactionsByDateRange(startDate, endDate)

    override suspend fun insertTransaction(transaction: TransactionDataModel): String {
        val transactionId = localDataSource.insertTransaction(
            id = transaction.id,
            createdAt = transaction.createdAt,
        )

        transaction.items.forEach { item ->
            localDataSource.addItemToTransaction(
                transactionId = transactionId,
                productId = item.productId,
                quantity = item.quantity,
                unitPrice = item.unitPrice,
                discountPercent = item.discountPercent,
            )
        }

        return transactionId
    }

    override suspend fun insertTransactionWithItems(
        transactionId: String,
        createdAt: Instant,
        items: List<TransactionItemDataModel>,
    ) {
        localDataSource.insertTransaction(transactionId, createdAt)

        items.forEach { item ->
            localDataSource.addItemToTransaction(
                transactionId = transactionId,
                productId = item.productId,
                quantity = item.quantity,
                unitPrice = item.unitPrice,
                discountPercent = item.discountPercent,
            )
        }
    }

    override suspend fun addItemToTransaction(
        transactionId: String,
        productId: String?,
        quantity: Int,
        unitPrice: Int,
        discountPercent: Double,
    ) {
        localDataSource.addItemToTransaction(
            transactionId = transactionId,
            productId = productId,
            quantity = quantity,
            unitPrice = unitPrice,
            discountPercent = discountPercent,
        )
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
}
