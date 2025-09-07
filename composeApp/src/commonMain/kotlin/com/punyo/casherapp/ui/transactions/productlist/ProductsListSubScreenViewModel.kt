package com.punyo.casherapp.ui.transactions.productlist

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.lifecycle.viewModelScope
import com.punyo.casherapp.data.product.ProductRepository
import com.punyo.casherapp.data.transaction.TransactionRepository
import com.punyo.casherapp.data.transaction.model.TransactionDataModel
import com.punyo.casherapp.ui.transactions.BaseTransactionsSubScreenUiState
import com.punyo.casherapp.ui.transactions.BaseTransactionsSubScreenViewModel
import com.punyo.casherapp.ui.transactions.ProductSummary
import kotlinx.coroutines.launch
import kotlinx.datetime.Instant

@OptIn(ExperimentalMaterial3Api::class)
class ProductsListSubScreenViewModel(
    private val transactionRepository: TransactionRepository,
    private val productRepository: ProductRepository,
) : BaseTransactionsSubScreenViewModel<ProductsListScreenData>() {

    override fun createInitialState(): BaseTransactionsSubScreenUiState<ProductsListScreenData> = BaseTransactionsSubScreenUiState()

    override fun loadDataForDateRange(startDateMillis: Long?, endDateMillis: Long?, searchQuery: String) {
        viewModelScope.launch {
            val transactionsFlow = if (startDateMillis != null && endDateMillis != null) {
                val startInstant = Instant.fromEpochMilliseconds(startDateMillis)
                val endInstant = Instant.fromEpochMilliseconds(endDateMillis)
                transactionRepository.searchTransactions(startInstant, endInstant)
            } else {
                transactionRepository.getAllTransactions()
            }

            transactionsFlow.collect { transactions ->
                val productSummaries = getProductSummariesByTransactionDataModels(transactions)
                val filteredProducts = getFilteredProductsBySearchText(productSummaries, searchQuery)
                state.value = state.value.copy(
                    data = ProductsListScreenData(
                        products = productSummaries,
                        filteredProducts = filteredProducts,
                    ),
                )
            }
        }
    }

    override fun updateData() {
        loadDataForDateRange(
            getAdjustedStartDateMillis(),
            getAdjustedEndDateMillis(),
            state.value.searchText,
        )
    }

    override fun setSearchText(text: String) {
        state.value = state.value.copy(searchText = text)
        state.value.data?.let { data ->
            state.value = state.value.copy(
                data = data.copy(
                    filteredProducts = getFilteredProductsBySearchText(data.products, text),
                ),
            )
        }
    }

    private fun getFilteredProductsBySearchText(
        products: List<ProductSummary>,
        searchText: String,
    ): List<ProductSummary>? = if (searchText.isBlank()) {
        products
    } else {
        products.filter { product ->
            product.name.contains(searchText, ignoreCase = true) ||
                (product.productId?.contains(searchText, ignoreCase = true) == true)
        }
    }

    private suspend fun getProductSummariesByTransactionDataModels(transactions: List<TransactionDataModel>): List<ProductSummary> {
        val productMap = mutableMapOf<String, ProductSummary>()
        transactions
            .flatMap { it.items }
            .forEach { item ->
                if (item.productId == null) {
                    return@forEach
                }
                val productId = item.productId
                if (productMap.containsKey(productId)) {
                    val existingSummary = productMap[productId]!!
                    val updatedSummary = existingSummary.copy(
                        totalQuantity = existingSummary.totalQuantity + item.quantity,
                        totalRevenue = existingSummary.totalRevenue + item.totalPrice,
                    )
                    productMap[productId] = updatedSummary
                } else {
                    val productData = productRepository.getProductById(item.productId)!!
                    val newSummary = ProductSummary(
                        productId = item.productId,
                        name = productData.name,
                        unitPrice = productData.price,
                        totalQuantity = item.quantity,
                        totalRevenue = item.totalPrice,
                    )
                    productMap[productId] = newSummary
                }
            }
        return productMap.values.map {
            ProductSummary(
                productId = it.productId,
                name = it.name,
                unitPrice = it.unitPrice,
                totalQuantity = it.totalQuantity,
                totalRevenue = it.totalRevenue,
            )
        }.sortedByDescending { it.totalRevenue }
    }
}

data class ProductsListScreenData(
    val products: List<ProductSummary>,
    val filteredProducts: List<ProductSummary>?,
)
