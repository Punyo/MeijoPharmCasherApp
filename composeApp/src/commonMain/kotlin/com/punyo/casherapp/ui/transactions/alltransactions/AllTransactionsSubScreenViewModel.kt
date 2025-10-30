package com.punyo.casherapp.ui.transactions.alltransactions

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.lifecycle.viewModelScope
import com.punyo.casherapp.data.transaction.TransactionRepository
import com.punyo.casherapp.data.transaction.model.TransactionDataModel
import com.punyo.casherapp.ui.transactions.BaseTransactionsSubScreenUiState
import com.punyo.casherapp.ui.transactions.BaseTransactionsSubScreenViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.datetime.Instant

@OptIn(ExperimentalMaterial3Api::class)
class AllTransactionsSubScreenViewModel(
    private val transactionRepository: TransactionRepository,
) : BaseTransactionsSubScreenViewModel<List<TransactionDataModel>>() {

    override fun createInitialState(): BaseTransactionsSubScreenUiState<List<TransactionDataModel>> = BaseTransactionsSubScreenUiState()

    override fun loadDataForDateRange(
        startDateMillis: Long?,
        endDateMillis: Long?,
        searchQuery: String,
    ) {
        searchJob?.cancel()
        searchJob = viewModelScope.launch {
            state.value = state.value.copy(data = null)
            val startInstant = startDateMillis?.let { Instant.fromEpochMilliseconds(it) }
            val endInstant = endDateMillis?.let { Instant.fromEpochMilliseconds(it) }

            transactionRepository.searchTransactions(
                startDate = startInstant,
                endDate = endInstant,
                searchQuery = searchQuery.ifBlank { null },
            ).collectLatest { transactions ->
                state.value = state.value.copy(data = transactions)
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

    fun deleteTransaction(transactionId: String) {
        viewModelScope.launch {
            transactionRepository.deleteTransaction(transactionId)
            updateData()
        }
    }
}
