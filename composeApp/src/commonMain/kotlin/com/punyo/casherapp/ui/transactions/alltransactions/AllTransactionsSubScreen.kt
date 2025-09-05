package com.punyo.casherapp.ui.transactions.alltransactions

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.produceState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.punyo.casherapp.data.product.ProductRepository
import com.punyo.casherapp.data.transaction.model.TransactionDataModel
import com.punyo.casherapp.extensions.toDateString
import com.punyo.casherapp.ui.component.DateRangePickerDialog
import com.punyo.casherapp.ui.component.NavigateBackButton
import com.punyo.casherapp.ui.component.SearchAndDateFilterTextField
import com.punyo.casherapp.ui.transactions.TimePeriod
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toInstant
import kotlinx.datetime.toLocalDateTime
import org.koin.compose.koinInject

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AllTransactionsSubScreen(
    timePeriod: TimePeriod = TimePeriod.TODAY,
    viewModel: AllTransactionsSubScreenViewModel = koinInject(),
    onNavigateBack: () -> Unit,
) {
    val uiState by viewModel.uiState.collectAsState()
    val dateRangePickerState = uiState.dateRangePickerState

    LaunchedEffect(timePeriod) {
        if (timePeriod == TimePeriod.TODAY) {
            val timeZone = TimeZone.currentSystemDefault()
            val utcMillis = Clock.System.now().toLocalDateTime(timeZone).toInstant(TimeZone.UTC).toEpochMilliseconds()
            dateRangePickerState.setSelection(
                startDateMillis = utcMillis,
                endDateMillis = utcMillis + viewModel.oneDay,
            )
            viewModel.setDateRange()
        }
    }

    LaunchedEffect(dateRangePickerState.selectedStartDateMillis, dateRangePickerState.selectedEndDateMillis) {
        viewModel.setDateRange()
    }

    val displayTransactions = uiState.transactions

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
            placeholderText = "取引に含まれている商品名で検索",
            onShowDatePickerDialog = { viewModel.setShowDatePickerDialog(it) },
            dateRangePickerState = dateRangePickerState,
        )

        Column(
            modifier = Modifier.fillMaxWidth(),
        ) {
            if (uiState.isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.CenterHorizontally),
                )
            } else if (uiState.error != null) {
                Text(
                    text = "エラー: ${uiState.error}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.align(Alignment.CenterHorizontally),
                )
            } else {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        text = "取引一覧",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                    )
                }

                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    items(displayTransactions.size) { index ->
                        val transaction = displayTransactions[index]
                        if (index >= displayTransactions.size - 3 && uiState.hasMorePages && !uiState.isLoadingMore) {
                            LaunchedEffect(displayTransactions.size) {
                                viewModel.loadMoreTransactions()
                            }
                        }

                        DetailedTransactionItem(
                            transaction = transaction,
                            productRepository = koinInject(),
                        )
                    }

                    // ローディングインジケーター
                    if (uiState.isLoadingMore) {
                        item {
                            CircularProgressIndicator(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .wrapContentWidth(Alignment.CenterHorizontally)
                                    .padding(16.dp),
                            )
                        }
                    }
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
fun DetailedTransactionItem(
    transaction: TransactionDataModel,
    productRepository: ProductRepository,
) {
    val dateTime = transaction.createdAt.toLocalDateTime(TimeZone.currentSystemDefault())

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
                    text = dateTime.toDateString(),
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
                text = transaction.id,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(bottom = 8.dp),
            )

            Column(
                verticalArrangement = Arrangement.spacedBy(4.dp),
                modifier = Modifier.padding(bottom = 8.dp),
            ) {
                transaction.items.forEach { item ->
                    val productName by produceState(
                        initialValue = "読み込み中...",
                        key1 = item.productId,
                    ) {
                        value = item.productId?.let { productId ->
                            productRepository.getProductById(productId)?.name
                        } ?: "不明な商品"
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "$productName × ${item.quantity}",
                                style = MaterialTheme.typography.bodySmall,
                            )
                            if (item.discountPercent != 0.0) {
                                Text(
                                    text = "${item.discountPercent}% 割引",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.error,
                                )
                            }
                        }
                        Text(
                            text = "¥${item.totalPrice}",
                            style = MaterialTheme.typography.bodySmall,
                            fontWeight = FontWeight.Medium,
                            color = if (item.discountPercent != 0.0) {
                                MaterialTheme.colorScheme.error
                            } else {
                                MaterialTheme.colorScheme.onSurface
                            },
                        )
                    }
                }
            }
        }
    }
}
