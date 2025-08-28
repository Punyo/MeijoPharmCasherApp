package com.punyo.casherapp.ui.transactions

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDateRangePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.punyo.casherapp.ui.component.DateRangePickerDialog
import com.punyo.casherapp.ui.component.NavigateBackButton

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AllTransactionsScreen(
    onNavigateBack: () -> Unit,
) {
    var searchText by remember { mutableStateOf("") }
    var showDatePickerDialog by remember { mutableStateOf(false) }
    val dateRangePickerState = rememberDateRangePickerState()

    val allTransactions = generateMockTransactions(multiplier = 30)

    val filteredTransactions = allTransactions.filter { transaction ->
        val matchesSearch = if (searchText.isBlank()) {
            true
        } else {
            transaction.id.contains(searchText, ignoreCase = true) ||
                transaction.items.any { it.name.contains(searchText, ignoreCase = true) }
        }

        matchesSearch
    }.sortedByDescending { it.timestamp }
    val isRangeSelected = dateRangePickerState.selectedStartDateMillis != null &&
        dateRangePickerState.selectedEndDateMillis != null

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
    ) {
        NavigateBackButton(
            onNavigateBack = onNavigateBack,
            text = "全ての取引",
        )
        Row(
            modifier = Modifier.padding(vertical = 8.dp).fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            OutlinedTextField(
                value = searchText,
                onValueChange = {
                    searchText = it
                },
                placeholder = { Text("商品名またはバーコードで検索") },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "検索",
                    )
                },
                modifier = Modifier.weight(1f).height(56.dp),
                singleLine = true,
            )

            IconButton(onClick = { showDatePickerDialog = true }) {
                Icon(
                    imageVector = Icons.Default.CalendarMonth,
                    contentDescription = "日付範囲選択",
                )
            }
            if (searchText.isNotEmpty() || isRangeSelected) {
                IconButton(
                    onClick = {
                        searchText = ""
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
                        val startDate = java.time.Instant.ofEpochMilli(startMillis)
                            .atZone(java.time.ZoneId.systemDefault()).toLocalDate()
                        startDate.toString()
                    } ?: "開始日未選択"
                } 〜 ${
                    dateRangePickerState.selectedEndDateMillis?.let { endMillis ->
                        val endDate = java.time.Instant.ofEpochMilli(endMillis)
                            .atZone(java.time.ZoneId.systemDefault()).toLocalDate()
                        endDate.toString()
                    } ?: "終了日未選択"
                }",
                style = MaterialTheme.typography.bodySmall,
            )
        }

        Column(
            modifier = Modifier.fillMaxWidth(),
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = "取引一覧 (${filteredTransactions.size}件)",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                )
                Text(
                    text = "総額: ¥${
                        filteredTransactions.sumOf { it.totalAmount }.toString().reversed().chunked(3)
                            .joinToString(",").reversed()
                    }",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold,
                )
            }

            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                items(filteredTransactions.size) { index ->
                    val transaction = filteredTransactions[index]
                    DetailedTransactionItem(transaction)
                }
            }
        }

        if (showDatePickerDialog) {
            DateRangePickerDialog(
                onDismiss = { showDatePickerDialog = false },
                dateRangePickerState = dateRangePickerState,
            )
        }
    }
}

@Composable
fun DetailedTransactionItem(transaction: Transaction) {
    ElevatedCard(
        modifier = Modifier.fillMaxWidth(),
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = transaction.id,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                )
                Text(
                    text = "¥${transaction.totalAmount}",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary,
                )
            }

            Text(
                text = "${
                    transaction.timestamp.hour.toString().padStart(2, '0')
                }:${transaction.timestamp.minute.toString().padStart(2, '0')}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(bottom = 8.dp),
            )

            Column(
                verticalArrangement = Arrangement.spacedBy(4.dp),
                modifier = Modifier.padding(bottom = 8.dp),
            ) {
                transaction.items.forEach { item ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                    ) {
                        Text(
                            text = "${item.name} × ${item.quantity}",
                            style = MaterialTheme.typography.bodySmall,
                            modifier = Modifier.weight(1f),
                        )
                        Text(
                            text = "¥${item.totalPrice}",
                            style = MaterialTheme.typography.bodySmall,
                            fontWeight = FontWeight.Medium,
                        )
                    }
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Text(
                    text = when (transaction.paymentMethod) {
                        PaymentMethod.CASH -> "現金"
                        PaymentMethod.CARD -> "カード"
                        PaymentMethod.QR_CODE -> "QR決済"
                    },
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                if (transaction.discount > 0) {
                    Text(
                        text = "割引: ${transaction.discount.toInt()}%",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.error,
                    )
                }
            }
        }
    }
}
