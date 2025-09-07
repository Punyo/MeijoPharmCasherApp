package com.punyo.casherapp.ui.component

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DateRangePicker
import androidx.compose.material3.DateRangePickerState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDateRangePickerState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DateRangePickerDialog(
    onConfirm: (startDateMillis: Long?, endDateMillis: Long?) -> Unit,
    onDismiss: () -> Unit,
    dateRangePickerState: DateRangePickerState,
) {
    val tempDateRangePickerState = rememberDateRangePickerState(
        initialSelectedStartDateMillis = dateRangePickerState.selectedStartDateMillis,
        initialSelectedEndDateMillis = dateRangePickerState.selectedEndDateMillis,
    )
    DatePickerDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(
                onClick = {
                    onConfirm(
                        tempDateRangePickerState.selectedStartDateMillis,
                        tempDateRangePickerState.selectedEndDateMillis,
                    )
                },
            ) {
                Text("OK")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        },
    ) {
        DateRangePicker(
            state = tempDateRangePickerState,
            title = {
                Text(
                    text = "日時の範囲を選択",
                )
            },
            showModeToggle = false,
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
        )
    }
}
