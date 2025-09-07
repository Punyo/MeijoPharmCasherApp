package com.punyo.casherapp.ui.transactions

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.DateRangePickerState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.punyo.casherapp.ui.component.DateRangePickerDialog
import com.punyo.casherapp.ui.component.NavigateBackButton
import com.punyo.casherapp.ui.component.SearchAndDateFilterTextField
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toInstant
import kotlinx.datetime.toLocalDateTime

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BaseTransactionSubScreen(
    timePeriod: TimePeriod = TimePeriod.TODAY,
    onNavigateBack: () -> Unit,
    navigationTitle: String,
    searchPlaceholder: String,
    searchText: String,
    showDatePickerDialog: Boolean,
    dateRangePickerState: DateRangePickerState,
    onLoadDataForDateRange: (TimePeriod) -> Unit,
    onSearchTextChange: (String) -> Unit,
    onSearchQueryClearButtonClick: () -> Unit,
    onShowDatePickerDialogButtonClick: (Boolean) -> Unit,
    onDateRangePickerConfirm: (Long?, Long?) -> Unit,
    content: @Composable () -> Unit,
) {
    LaunchedEffect(timePeriod) {
        if (timePeriod == TimePeriod.TODAY) {
            val timeZone = TimeZone.currentSystemDefault()
            val utcMillis = Clock.System.now().toLocalDateTime(timeZone).toInstant(TimeZone.UTC).toEpochMilliseconds()
            dateRangePickerState.setSelection(
                startDateMillis = utcMillis,
                endDateMillis = utcMillis,
            )
        }
        onLoadDataForDateRange(timePeriod)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
    ) {
        NavigateBackButton(
            onNavigateBack = onNavigateBack,
            text = navigationTitle,
        )

        SearchAndDateFilterTextField(
            searchText = searchText,
            placeholderText = searchPlaceholder,
            onSearchTextChange = onSearchTextChange,
            onSearchQueryClearButtonClick = onSearchQueryClearButtonClick,
            onShowDatePickerDialogButtonClick = { onShowDatePickerDialogButtonClick(true) },
            dateRangePickerState = dateRangePickerState,
        )

        content()
    }

    if (showDatePickerDialog) {
        DateRangePickerDialog(
            onConfirm = onDateRangePickerConfirm,
            onDismiss = { onShowDatePickerDialogButtonClick(false) },
            dateRangePickerState = dateRangePickerState,
        )
    }
}
