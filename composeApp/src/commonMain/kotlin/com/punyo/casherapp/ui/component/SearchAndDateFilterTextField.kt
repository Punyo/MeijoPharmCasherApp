package com.punyo.casherapp.ui.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.DateRangePickerState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import java.time.Instant
import java.time.ZoneId

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchAndDateFilterTextField(
    searchText: String,
    onSearchTextChange: (String) -> Unit,
    placeholderText: String,
    onShowDatePickerDialog: (Boolean) -> Unit,
    dateRangePickerState: DateRangePickerState,
    modifier: Modifier = Modifier,
) {
    val isRangeSelected = dateRangePickerState.selectedStartDateMillis != null ||
        dateRangePickerState.selectedEndDateMillis != null
    Column(modifier = modifier) {
        Row(
            modifier = Modifier.padding(vertical = 8.dp).fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            OutlinedTextField(
                value = searchText,
                onValueChange = onSearchTextChange,
                placeholder = { Text(placeholderText) },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "検索",
                    )
                },
                modifier = Modifier.weight(1f).height(56.dp),
                singleLine = true,
            )
            IconButton(onClick = { onShowDatePickerDialog(true) }) {
                Icon(
                    imageVector = Icons.Default.CalendarMonth,
                    contentDescription = "日付範囲選択",
                )
            }
            if (searchText.isNotEmpty() || isRangeSelected) {
                IconButton(
                    onClick = {
                        onSearchTextChange("")
                        dateRangePickerState.setSelection(
                            startDateMillis = null,
                            endDateMillis = null,
                        )
                    },
                ) {
                    Icon(
                        imageVector = Icons.Default.Clear,
                        contentDescription = "クリア",
                    )
                }
            }
        }
        if (isRangeSelected) {
            Text(
                modifier = Modifier.padding(bottom = 8.dp).fillMaxWidth(),
                text = "選択中: ${
                    dateRangePickerState.selectedStartDateMillis?.let { startMillis ->
                        val startDate = Instant.ofEpochMilli(startMillis)
                            .atZone(ZoneId.of("UTC")).toLocalDate()
                        startDate.toString()
                    } ?: "開始日未選択"
                } 〜 ${
                    dateRangePickerState.selectedEndDateMillis?.let { endMillis ->
                        val endDate = Instant.ofEpochMilli(endMillis)
                            .atZone(ZoneId.of("UTC")).toLocalDate()
                        endDate.toString()
                    } ?: "終了日未選択"
                }",
                style = MaterialTheme.typography.bodySmall,
            )
        }
    }
}
