package com.punyo.casherapp.ui.transactions

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.offsetAt
import kotlinx.datetime.toInstant
import kotlinx.datetime.toLocalDateTime

@OptIn(ExperimentalMaterial3Api::class)
abstract class BaseTransactionsSubScreenViewModel<T> : ViewModel() {

    protected val state = MutableStateFlow(createInitialState())
    protected var searchJob: Job? = null

    val uiState: StateFlow<BaseTransactionsSubScreenUiState<T>> = state.asStateFlow()

    val oneDayMillis = 86399999L

    protected abstract fun createInitialState(): BaseTransactionsSubScreenUiState<T>

    protected abstract fun loadDataForDateRange(
        startDateMillis: Long?,
        endDateMillis: Long?,
        searchQuery: String,
    )

    protected abstract fun updateData()

    fun setShowDatePickerDialog(show: Boolean) {
        state.value = state.value.copy(showDatePickerDialog = show)
    }

    open fun setSearchText(text: String) {
        state.value = state.value.copy(searchText = text)
        updateData()
    }

    fun onSearchQueryClearButtonClick() {
        state.value = state.value.copy(searchText = "")
        uiState.value.dateRangePickerState.setSelection(null, null)
        updateData()
    }

    fun onDateRangePickerConfirm(startDateMillis: Long?, endDateMillis: Long?) {
        state.value = state.value.copy(showDatePickerDialog = false)
        state.value.dateRangePickerState.setSelection(startDateMillis, endDateMillis)
        updateData()
    }

    fun loadDataForDateRange(timePeriod: TimePeriod) {
        if (timePeriod == TimePeriod.TODAY) {
            val timeZone = TimeZone.currentSystemDefault()
            val utcMillis =
                Clock.System.now().toLocalDateTime(timeZone).toInstant(TimeZone.UTC).toEpochMilliseconds()
            state.value.dateRangePickerState.setSelection(
                startDateMillis = utcMillis,
                endDateMillis = utcMillis,
            )
        }
        updateData()
    }

    protected fun calculateOffsetMillis(): Long = TimeZone.currentSystemDefault()
        .offsetAt(Clock.System.now())
        .totalSeconds * 1000L

    protected fun getAdjustedStartDateMillis(): Long? = state.value.dateRangePickerState.selectedStartDateMillis?.minus(calculateOffsetMillis())

    protected fun getAdjustedEndDateMillis(): Long? = state.value.dateRangePickerState.selectedEndDateMillis?.minus(calculateOffsetMillis())?.plus(oneDayMillis)
}
