package com.punyo.casherapp.ui.transactions.productlist

import androidx.compose.material3.DateRangePickerState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.punyo.casherapp.data.product.ProductRepository
import com.punyo.casherapp.data.transaction.TransactionRepository
import com.punyo.casherapp.data.transaction.model.TransactionDataModel
import com.punyo.casherapp.ui.transactions.ProductSummary
import com.punyo.casherapp.ui.transactions.TimePeriod
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.offsetAt
import kotlinx.datetime.toInstant
import kotlinx.datetime.toLocalDateTime
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
class ProductsListSubScreenViewModel(
    private val transactionRepository: TransactionRepository,
    private val productRepository: ProductRepository,
) : ViewModel() {
    private val state = MutableStateFlow(ProductsListScreenUiState())
    val uiState: StateFlow<ProductsListScreenUiState> = state.asStateFlow()

    val oneDayMillis = 86399999L

    fun loadProductsForDateRange(startDateMillis: Long?, endDateMillis: Long?) {
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
                state.value = state.value.copy(
                    products = productSummaries,
                    filteredProducts = getFilteredProductsBySearchText(productSummaries, state.value.searchText),
                )
            }
        }
    }

    fun onSearchQueryClearButtonClick() {
        viewModelScope.launch {
            state.value = state.value.copy(
                searchText = "",
            )
            uiState.value.dateRangePickerState.setSelection(null, null)
        }
        updateProducts()
    }

    fun onDateRangePickerConfirm(startDateMillis: Long?, endDateMillis: Long?) {
        state.value = state.value.copy(showDatePickerDialog = false)
        state.value.dateRangePickerState.setSelection(
            startDateMillis = startDateMillis,
            endDateMillis = endDateMillis,
        )
        updateProducts()
    }

    fun loadProductsForDateRange(timePeriod: TimePeriod) {
        viewModelScope.launch {
            if (timePeriod == TimePeriod.TODAY) {
                val timeZone = TimeZone.currentSystemDefault()
                val utcMillis =
                    Clock.System.now().toLocalDateTime(timeZone).toInstant(TimeZone.UTC).toEpochMilliseconds()
                state.value.dateRangePickerState.setSelection(
                    startDateMillis = utcMillis,
                    endDateMillis = utcMillis,
                )
            }
        }
        updateProducts()
    }

    fun updateProducts() {
        val offsetMillis = TimeZone.currentSystemDefault()
            .offsetAt(Clock.System.now())
            .totalSeconds * 1000L

        loadProductsForDateRange(
            state.value.dateRangePickerState.selectedStartDateMillis?.minus(offsetMillis),
            state.value.dateRangePickerState.selectedEndDateMillis?.minus(offsetMillis)?.plus(oneDayMillis),
        )
    }

    fun setSearchText(text: String) {
        state.value = state.value.copy(searchText = text)
        state.value.products?.let {
            state.value = state.value.copy(
                filteredProducts = getFilteredProductsBySearchText(it, text),
            )
        }
    }

    fun setShowDatePickerDialog(show: Boolean) {
        state.value = state.value.copy(showDatePickerDialog = show)
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

data class ProductsListScreenUiState
@OptIn(ExperimentalMaterial3Api::class)
constructor(
    val searchText: String = "",
    val showDatePickerDialog: Boolean = false,
    val products: List<ProductSummary>? = null,
    val filteredProducts: List<ProductSummary>? = null,
    val dateRangePickerState: DateRangePickerState = DateRangePickerState(
        locale = Locale.getDefault(),
    ),
)
