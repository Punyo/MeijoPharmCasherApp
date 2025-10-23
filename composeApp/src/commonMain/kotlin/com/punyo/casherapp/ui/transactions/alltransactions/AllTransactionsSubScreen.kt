package com.punyo.casherapp.ui.transactions.alltransactions

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.produceState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.punyo.casherapp.data.product.ProductRepository
import com.punyo.casherapp.data.transaction.model.TransactionDataModel
import com.punyo.casherapp.extensions.format
import com.punyo.casherapp.extensions.toDateString
import com.punyo.casherapp.ui.transactions.BaseTransactionSubScreen
import com.punyo.casherapp.ui.transactions.TimePeriod
import kotlinx.datetime.TimeZone
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

    BaseTransactionSubScreen(
        timePeriod = timePeriod,
        onNavigateBack = onNavigateBack,
        navigationTitle = "全ての取引",
        searchPlaceholder = "取引に含まれている商品名で検索",
        searchText = uiState.searchText,
        showDatePickerDialog = uiState.showDatePickerDialog,
        dateRangePickerState = uiState.dateRangePickerState,
        onLoadDataForDateRange = viewModel::loadDataForDateRange,
        onSearchTextChange = viewModel::setSearchText,
        onSearchQueryClearButtonClick = viewModel::onSearchQueryClearButtonClick,
        onShowDatePickerDialogButtonClick = viewModel::setShowDatePickerDialog,
        onDateRangePickerConfirm = viewModel::onDateRangePickerConfirm,
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth(),
        ) {
            if (uiState.data != null) {
                val transactions = uiState.data!!
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
                    items(transactions.size) { index ->
                        val transaction = transactions[index]

                        DetailedTransactionItem(
                            transaction = transaction,
                            productRepository = koinInject(),
                        )
                    }
                }
            } else {
                CircularProgressIndicator(
                    modifier = Modifier
                        .fillMaxWidth()
                        .wrapContentWidth(Alignment.CenterHorizontally)
                        .padding(32.dp),
                )
            }
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
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                Text(
                    text = transaction.totalAmount.format(),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }

            Text(
                text = transaction.id,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(bottom = 8.dp),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
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
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                            )
                            if (item.discountAmount.isPositive) {
                                Text(
                                    text = "ひとつ当たり ${item.discountAmount.format()} 割引",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.error,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis,
                                )
                            }
                        }
                        Text(
                            text = item.totalPrice.format(),
                            style = MaterialTheme.typography.bodySmall,
                            fontWeight = FontWeight.Medium,
                            color = if (item.discountAmount.isPositive) {
                                MaterialTheme.colorScheme.error
                            } else {
                                MaterialTheme.colorScheme.onSurface
                            },
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                        )
                    }
                }
            }
        }
    }
}
