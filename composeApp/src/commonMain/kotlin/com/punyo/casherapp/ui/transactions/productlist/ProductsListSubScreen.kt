package com.punyo.casherapp.ui.transactions.productlist

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.punyo.casherapp.ui.component.DateRangePickerDialog
import com.punyo.casherapp.ui.component.NavigateBackButton
import com.punyo.casherapp.ui.component.SearchAndDateFilterTextField
import com.punyo.casherapp.ui.transactions.ProductSummary
import com.punyo.casherapp.ui.transactions.TimePeriod
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toInstant
import kotlinx.datetime.toLocalDateTime
import org.koin.compose.koinInject

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductsListSubScreen(
    timePeriod: TimePeriod = TimePeriod.TODAY,
    viewModel: ProductsListSubScreenViewModel = koinInject(),
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
                endDateMillis = utcMillis,
            )
        }
        viewModel.loadProductsForDateRange(timePeriod)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
    ) {
        NavigateBackButton(
            onNavigateBack = onNavigateBack,
            text = "全ての商品",
        )

        SearchAndDateFilterTextField(
            searchText = uiState.searchText,
            placeholderText = "商品名またはIDで検索",
            onSearchTextChange = { viewModel.setSearchText(it) },
            onSearchQueryClearButtonClick = { viewModel.onSearchQueryClearButtonClick() },
            onShowDatePickerDialogButtonClick = { viewModel.setShowDatePickerDialog(true) },
            dateRangePickerState = dateRangePickerState,
        )

        Column(
            modifier = Modifier.fillMaxWidth(),
        ) {
            if (uiState.filteredProducts == null) {
                CircularProgressIndicator(
                    modifier = Modifier
                        .fillMaxWidth()
                        .wrapContentWidth(Alignment.CenterHorizontally)
                        .padding(32.dp),
                )
            } else {
                val filteredProducts = uiState.filteredProducts!!
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        text = "商品一覧 (${filteredProducts.size}件)",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                    )
                    Text(
                        text = "総売上: ¥${
                        filteredProducts.sumOf { it.totalRevenue }.toString().reversed().chunked(3)
                            .joinToString(",").reversed()
                        }",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Bold,
                    )
                }

                LazyVerticalGrid(
                    columns = GridCells.Adaptive(minSize = 400.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    items(filteredProducts.size) { index ->
                        val product = filteredProducts[index]
                        DetailedProductItem(product)
                    }
                }
            }
        }
    }

    if (uiState.showDatePickerDialog) {
        DateRangePickerDialog(
            onConfirm = viewModel::onDateRangePickerConfirm,
            onDismiss = { viewModel.setShowDatePickerDialog(false) },
            dateRangePickerState = dateRangePickerState,
        )
    }
}

@Composable
fun DetailedProductItem(product: ProductSummary) {
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
                    text = product.name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                )
                Text(
                    text = "¥${
                    product.totalRevenue.toString().reversed().chunked(3)
                        .joinToString(",").reversed()
                    }",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary,
                )
            }

            Text(
                text = "ID: ${product.productId}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(bottom = 8.dp),
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Text(
                    text = "売上数量: ${product.totalQuantity}個",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium,
                )
                Text(
                    text = "単価: ¥${product.unitPrice}",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.secondary,
                )
            }
        }
    }
}
