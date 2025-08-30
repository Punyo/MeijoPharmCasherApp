package com.punyo.casherapp.ui.transactions

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
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
import com.punyo.casherapp.ui.component.SearchAndDateFilterTextField

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductsListSubScreen(
    onNavigateBack: () -> Unit,
) {
    var searchText by remember { mutableStateOf("") }

    val allProducts = generateMockProductSummary(multiplier = 30)
    var showDatePickerDialog by remember { mutableStateOf(false) }

    val filteredProducts = allProducts.filter { product ->
        if (searchText.isBlank()) {
            true
        } else {
            product.name.contains(searchText, ignoreCase = true)
//                product.productId.contains(searchText, ignoreCase = true)
        }
    }.sortedByDescending { it.totalRevenue }

    val dateRangePickerState = rememberDateRangePickerState()

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
            searchText = searchText,
            onSearchTextChange = { searchText = it },
            placeholderText = "商品名またはIDで検索",
            onShowDatePickerDialog = { showDatePickerDialog = it },
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

    if (showDatePickerDialog) {
        DateRangePickerDialog(
            onDismiss = { showDatePickerDialog = false },
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
