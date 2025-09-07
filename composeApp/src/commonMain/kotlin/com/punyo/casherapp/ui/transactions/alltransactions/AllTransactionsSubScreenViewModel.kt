package com.punyo.casherapp.ui.transactions.alltransactions

import androidx.compose.material3.DateRangePickerState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.punyo.casherapp.data.transaction.TransactionRepository
import com.punyo.casherapp.data.transaction.model.TransactionDataModel
import com.punyo.casherapp.ui.transactions.TimePeriod
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.offsetAt
import kotlinx.datetime.toInstant
import kotlinx.datetime.toLocalDateTime
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
class AllTransactionsSubScreenViewModel(
    private val transactionRepository: TransactionRepository,
) : ViewModel() {
    private val state = MutableStateFlow(AllTransactionsScreenUiState())
    private var searchJob: Job? = null

    val uiState: StateFlow<AllTransactionsScreenUiState> = state.asStateFlow()

    val oneDayMillis = 86399999L

    fun loadTransactionsForDateRange(
        startDateMillis: Long?,
        endDateMillis: Long?,
        searchQuery: String,
    ) {
        searchJob?.cancel()
        searchJob = viewModelScope.launch {
            state.value = state.value.copy(transactions = null)
            val startInstant = startDateMillis?.let { Instant.fromEpochMilliseconds(it) }
            val endInstant = endDateMillis?.let { Instant.fromEpochMilliseconds(it) }

            transactionRepository.searchTransactions(
                startDate = startInstant,
                endDate = endInstant,
                searchQuery = searchQuery.ifBlank { null },
            ).collectLatest { transactions ->
                state.value = state.value.copy(transactions = transactions)
            }
        }
    }

    fun setShowDatePickerDialog(boolean: Boolean) {
        state.value = state.value.copy(showDatePickerDialog = boolean)
    }

    fun setSearchText(text: String) {
        state.value = state.value.copy(searchText = text)
        updateTransactions()
    }

    fun onSearchQueryClearButtonClick() {
        state.value = state.value.copy(searchText = "")
        uiState.value.dateRangePickerState.setSelection(null, null)
        updateTransactions()
    }

    fun onDateRangePickerConfirm(startDateMillis: Long?, endDateMillis: Long?) {
        state.value = state.value.copy(showDatePickerDialog = false)
        state.value.dateRangePickerState.setSelection(startDateMillis, endDateMillis)
        updateTransactions()
    }

    fun loadTransactionsForDateRange(timePeriod: TimePeriod) {
        if (timePeriod == TimePeriod.TODAY) {
            val timeZone = TimeZone.currentSystemDefault()
            val utcMillis =
                Clock.System.now().toLocalDateTime(timeZone).toInstant(TimeZone.UTC).toEpochMilliseconds()
            state.value.dateRangePickerState.setSelection(
                startDateMillis = utcMillis,
                endDateMillis = utcMillis,
            )
        }
        updateTransactions()
    }

    private fun updateTransactions() {
        val offsetMillis = TimeZone.currentSystemDefault()
            .offsetAt(Clock.System.now())
            .totalSeconds * 1000L

        loadTransactionsForDateRange(
            state.value.dateRangePickerState.selectedStartDateMillis?.minus(offsetMillis),
            state.value.dateRangePickerState.selectedEndDateMillis?.minus(offsetMillis)?.plus(oneDayMillis),
            state.value.searchText,
        )
    }
}

data class AllTransactionsScreenUiState
@OptIn(ExperimentalMaterial3Api::class)
constructor(
    val searchText: String = "",
    val showDatePickerDialog: Boolean = false,
    val transactions: List<TransactionDataModel>? = null,
    val dateRangePickerState: DateRangePickerState = DateRangePickerState(
        locale = Locale.getDefault(),
    ),
)
