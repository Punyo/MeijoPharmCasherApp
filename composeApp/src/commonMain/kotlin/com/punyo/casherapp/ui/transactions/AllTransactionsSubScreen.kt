package com.punyo.casherapp.ui.transactions

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDateRangePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.punyo.casherapp.ui.component.DateRangePickerDialog
import com.punyo.casherapp.ui.component.NavigateBackButton
import com.punyo.casherapp.ui.component.SearchAndDateFilterTextField
import org.koin.compose.koinInject

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AllTransactionsSubScreen(
    timePeriod: TimePeriod = TimePeriod.TODAY,
    viewModel: AllTransactionsSubScreenViewModel = koinInject(),
    onNavigateBack: () -> Unit,
) {
    val uiState by viewModel.uiState.collectAsState()
    val dateRangePickerState = rememberDateRangePickerState()

    // TimePeriodが変更された場合はViewModelに反映
    if (uiState.timePeriod != timePeriod) {
        viewModel.setTimePeriod(timePeriod)
    }

    val allTransactions = generateMockTransactions(multiplier = 30)

    val filteredTransactions = allTransactions.filter { transaction ->
        val matchesSearch = if (uiState.searchText.isBlank()) {
            true
        } else {
            transaction.id.contains(uiState.searchText, ignoreCase = true)
//                transaction.items.any { it.name.contains(uiState.searchText, ignoreCase = true) }
        }

        matchesSearch
    }.sortedByDescending { it.timestamp }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
    ) {
        NavigateBackButton(
            onNavigateBack = onNavigateBack,
            text = "全ての取引",
        )

        SearchAndDateFilterTextField(
            searchText = uiState.searchText,
            onSearchTextChange = { viewModel.setSearchText(it) },
            placeholderText = "商品名またはバーコードで検索",
            onShowDatePickerDialog = { viewModel.setShowDatePickerDialog(it) },
            dateRangePickerState = dateRangePickerState,
        )

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
//                Text(
//                    text = "総額: ¥${
//                        filteredTransactions.sumOf { it.totalAmount }.toString().reversed().chunked(3)
//                            .joinToString(",").reversed()
//                    }",
//                    style = MaterialTheme.typography.bodyMedium,
//                    color = MaterialTheme.colorScheme.primary,
//                    fontWeight = FontWeight.Bold,
//                )
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

        if (uiState.showDatePickerDialog) {
            DateRangePickerDialog(
                onDismiss = { viewModel.setShowDatePickerDialog(false) },
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
//                Text(
//                    text = "¥${transaction.totalAmount}",
//                    style = MaterialTheme.typography.titleMedium,
//                    fontWeight = FontWeight.Bold,
//                    color = MaterialTheme.colorScheme.primary,
//                )
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
                            text = "$item × ${item.quantity}",
                            style = MaterialTheme.typography.bodySmall,
                            modifier = Modifier.weight(1f),
                        )
//                        Text(
//                            text = "¥${item.totalPrice}",
//                            style = MaterialTheme.typography.bodySmall,
//                            fontWeight = FontWeight.Medium,
//                        )
                    }
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
//                Text(
//                    text = when (transaction.paymentMethod) {
//                        PaymentMethod.CASH -> "現金"
//                        PaymentMethod.CARD -> "カード"
//                        PaymentMethod.QR_CODE -> "QR決済"
//                    },
//                    style = MaterialTheme.typography.bodySmall,
//                    color = MaterialTheme.colorScheme.onSurfaceVariant,
//                )
//                if (transaction.discount > 0) {
//                    Text(
//                        text = "割引: ${transaction.discount.toInt()}%",
//                        style = MaterialTheme.typography.bodySmall,
//                        color = MaterialTheme.colorScheme.error,
//                    )
//                }
            }
        }
    }
}
