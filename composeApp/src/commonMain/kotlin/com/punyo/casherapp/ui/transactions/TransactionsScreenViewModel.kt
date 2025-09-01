package com.punyo.casherapp.ui.transactions

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.punyo.casherapp.data.product.ProductRepository
import com.punyo.casherapp.data.transaction.TransactionRepository
import com.punyo.casherapp.data.transaction.model.TransactionDataModel
import com.punyo.casherapp.extensions.endOfDay
import com.punyo.casherapp.extensions.startOfDay
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toInstant
import kotlinx.datetime.toLocalDateTime
import kotlinx.serialization.Serializable
import kotlin.collections.forEach

class TransactionsScreenViewModel(
    private val transactionRepository: TransactionRepository,
    private val productRepository: ProductRepository,
) : ViewModel() {
    private val state = MutableStateFlow(TransactionsScreenUiState())
    val uiState: StateFlow<TransactionsScreenUiState> = state.asStateFlow()

    var todayTransactionCollectJob: Job? = null
    var allTimeTransactionCollectJob: Job? = null

    init {
        when (state.value.currentPeriod) {
            TimePeriod.TODAY -> {
                startCollectTodayTransaction()
            }

            TimePeriod.ALL_TIME -> {
                startCollectAllTimeTransaction()
            }
        }
    }

    private fun startCollectTodayTransaction() {
        val today = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())
        todayTransactionCollectJob = viewModelScope.launch {
            transactionRepository.getTransactionsByDateRange(
                startDate = today.startOfDay().toInstant(TimeZone.currentSystemDefault()),
                endDate = today.endOfDay().toInstant(TimeZone.currentSystemDefault()),
            ).collect { transactions ->
                state.update {
                    it.copy(
                        collectedTransactions = transactions,
                        productSummaries = getProductSummariesByPeriod(
                            transactions,
                            TimePeriod.TODAY,
                        ),
                        onlyTodayDateLoaded = true,
                    )
                }
            }
        }
    }

    private fun startCollectAllTimeTransaction() {
        allTimeTransactionCollectJob = viewModelScope.launch {
            transactionRepository.getAllTransactions().collect { transactions ->
                state.update {
                    it.copy(
                        collectedTransactions = transactions,
                        productSummaries = getProductSummariesByPeriod(
                            transactions,
                            TimePeriod.ALL_TIME,
                        ),
                        onlyTodayDateLoaded = false,
                    )
                }
            }
        }
    }

    fun getTransactionsByPeriod(period: TimePeriod): List<TransactionDataModel> {
        val transactions = state.value.collectedTransactions ?: return emptyList()

        return if (state.value.onlyTodayDateLoaded && period == TimePeriod.TODAY) {
            transactions
        } else {
            when (period) {
                TimePeriod.TODAY -> {
                    filterTodayData(transactions)
                }

                TimePeriod.ALL_TIME -> transactions
            }
        }
    }

    private fun filterTodayData(transactions: List<TransactionDataModel>): List<TransactionDataModel> {
        val today = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())
        return transactions.filter { transaction ->
            val transactionDate = transaction.createdAt.toLocalDateTime(TimeZone.currentSystemDefault())
            transactionDate.year == today.year &&
                transactionDate.monthNumber == today.monthNumber &&
                transactionDate.dayOfMonth == today.dayOfMonth
        }
    }

    fun getCustomerCountByPeriod(period: TimePeriod): Int = getTransactionsByPeriod(period).size

    fun getTotalRevenueByPeriod(period: TimePeriod): Int = getTransactionsByPeriod(period).sumOf { transaction ->
        transaction.items.sumOf { item -> item.totalPrice }
    }

    fun getTotalQuantityByPeriod(period: TimePeriod): Int = getTransactionsByPeriod(period).sumOf { transaction ->
        transaction.items.sumOf { item -> item.quantity }
    }

    private suspend fun getProductSummariesByPeriod(
        transactions: List<TransactionDataModel>,
        period: TimePeriod,
    ): List<ProductSummary> {
        val productMap = mutableMapOf<String, ProductSummary>()

        val filteredTransactions = if (!state.value.onlyTodayDateLoaded && period == TimePeriod.TODAY) {
            filterTodayData(transactions)
        } else {
            transactions
        }

        filteredTransactions.forEach { transaction ->
            transaction.items.forEach { item ->
                if (item.productId != null) {
                    val existingSummary = productMap[item.productId]
                    if (existingSummary != null) {
                        val updatedSummary = existingSummary.copy(
                            totalQuantity = existingSummary.totalQuantity + item.quantity,
                            totalRevenue = existingSummary.totalRevenue + item.totalPrice,
                        )
                        productMap[item.productId] = updatedSummary
                    } else {
                        val newSummary = ProductSummary(
                            productId = item.productId,
                            name = productRepository.getProductById(item.productId)!!.name,
                            totalQuantity = item.quantity,
                            totalRevenue = item.totalPrice,
                            unitPrice = item.unitPrice,
                        )
                        productMap[item.productId] = newSummary
                    }
                }
            }
        }

        return productMap.values.sortedByDescending { it.totalRevenue }
    }

    fun setCurrentPeriod(period: TimePeriod) {
        if (period == TimePeriod.ALL_TIME) {
            if (todayTransactionCollectJob != null) {
                todayTransactionCollectJob?.cancel()
                todayTransactionCollectJob = null
            }
            if (allTimeTransactionCollectJob == null) {
                state.update {
                    it.copy(
                        collectedTransactions = null,
                    )
                }
                startCollectAllTimeTransaction()
            }
        }
        viewModelScope.launch {
            state.update {
                it.copy(
                    currentPeriod = period,
                    productSummaries = it.collectedTransactions?.let { transactions ->
                        getProductSummariesByPeriod(
                            transactions,
                            period,
                        )
                    },
                )
            }
        }
    }
}

data class TransactionsScreenUiState(
    val collectedTransactions: List<TransactionDataModel>? = null,
    val productSummaries: List<ProductSummary>? = null,
    val currentPeriod: TimePeriod = TimePeriod.TODAY,
    val onlyTodayDateLoaded: Boolean = false,
)

@Serializable
enum class TimePeriod {
    TODAY,
    ALL_TIME,
}

data class ProductSummary(
    val productId: String?,
    val name: String,
    val totalQuantity: Int,
    val totalRevenue: Int,
    val unitPrice: Int,
)
