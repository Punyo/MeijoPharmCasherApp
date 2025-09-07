package com.punyo.casherapp.ui.transactions

import androidx.compose.material3.DateRangePickerState
import androidx.compose.material3.ExperimentalMaterial3Api
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
data class BaseTransactionsSubScreenUiState<T>(
    val searchText: String = "",
    val showDatePickerDialog: Boolean = false,
    val data: T? = null,
    val dateRangePickerState: DateRangePickerState = DateRangePickerState(
        locale = Locale.getDefault(),
    ),
)
