package com.punyo.casherapp.ui.transactions.alltransactions

import androidx.compose.material3.DateRangePickerState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.punyo.casherapp.data.transaction.TransactionRepository
import com.punyo.casherapp.data.transaction.model.TransactionDataModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.offsetAt
import java.util.Locale
import kotlin.time.ExperimentalTime

@OptIn(ExperimentalMaterial3Api::class)
class AllTransactionsSubScreenViewModel(
    private val transactionRepository: TransactionRepository,
) : ViewModel() {
    private val state = MutableStateFlow(AllTransactionsScreenUiState())
    val uiState: StateFlow<AllTransactionsScreenUiState> = state.asStateFlow()

    val oneDay = 86_399_399L

    companion object {
        private const val PAGE_SIZE = 20
    }

    init {
        loadInitialTransactions()
    }

    private fun loadInitialTransactions() {
        viewModelScope.launch {
            state.value = state.value.copy(isLoading = true, transactions = emptyList())
            loadTransactionsPage(reset = true)
        }
    }

    @OptIn(ExperimentalTime::class)
    private suspend fun loadTransactionsPage(reset: Boolean = false) {
        val currentState = state.value
        val offset = if (reset) 0 else currentState.transactions.size
        val timeZone = TimeZone.currentSystemDefault()
        val offsetFromUTCMillis = timeZone.offsetAt(
            kotlinx.datetime.Clock.System.now(),
        ).totalSeconds * 1000

        try {
            transactionRepository.getTransactionsPaged(
                limit = PAGE_SIZE,
                offset = offset,
                startDate = currentState.dateRangePickerState.selectedStartDateMillis?.let {
                    Instant.fromEpochMilliseconds(it - offsetFromUTCMillis)
                },
                endDate = currentState.dateRangePickerState.selectedEndDateMillis
                    ?.let { Instant.fromEpochMilliseconds(it - offsetFromUTCMillis + oneDay) }
                    ?: Instant.DISTANT_FUTURE,
                searchQuery = currentState.searchText.ifBlank { null },
            ).collect { newTransactions ->
                val allTransactions = if (reset) {
                    newTransactions
                } else {
                    currentState.transactions + newTransactions
                }

                state.value = currentState.copy(
                    transactions = allTransactions,
                    hasMorePages = newTransactions.size == PAGE_SIZE,
                    isLoading = false,
                    isLoadingMore = false,
                )
            }
        } catch (e: Exception) {
            state.value = currentState.copy(
                isLoading = false,
                isLoadingMore = false,
                error = e.message,
            )
        }
    }

    fun loadMoreTransactions() {
        val currentState = state.value
        if (!currentState.isLoadingMore && currentState.hasMorePages) {
            viewModelScope.launch {
                state.value = currentState.copy(isLoadingMore = true)
                loadTransactionsPage(reset = false)
            }
        }
    }

    fun setShowDatePickerDialog(boolean: Boolean) {
        state.value = state.value.copy(showDatePickerDialog = boolean)
    }

    fun setSearchText(text: String) {
        viewModelScope.launch {
            state.value = state.value.copy(
                searchText = text,
                isLoading = true,
                transactions = emptyList(),
            )
            loadTransactionsPage(reset = true)
        }
    }

    fun setDateRange() {
        viewModelScope.launch {
            state.value = state.value.copy(
                isLoading = true,
                transactions = emptyList(),
            )
            loadTransactionsPage(reset = true)
        }
    }
}

data class AllTransactionsScreenUiState
@OptIn(ExperimentalMaterial3Api::class)
constructor(
    val searchText: String = "",
    val showDatePickerDialog: Boolean = false,
    val transactions: List<TransactionDataModel> = emptyList(),
    val isLoading: Boolean = true,
    val isLoadingMore: Boolean = false,
    val hasMorePages: Boolean = true,
    val error: String? = null,
    val dateRangePickerState: DateRangePickerState = DateRangePickerState(
        locale = Locale.getDefault(),
    ),
)
