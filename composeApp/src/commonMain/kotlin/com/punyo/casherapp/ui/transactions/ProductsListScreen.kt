package com.punyo.casherapp.ui.transactions

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductsListScreen(
    onNavigateBack: () -> Unit,
) {
    var searchText by remember { mutableStateOf("") }

    val allProducts = generateMockProductSummary(multiplier = 30)

    val filteredProducts = allProducts.filter { product ->
        if (searchText.isBlank()) {
            true
        } else {
            product.name.contains(searchText, ignoreCase = true) ||
                product.productId.contains(searchText, ignoreCase = true)
        }
    }.sortedByDescending { it.totalRevenue }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
    ) {
        Row(
            modifier = Modifier.height(IntrinsicSize.Min).clickable(
                onClick = onNavigateBack,
            ),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.secondary,
            )
            Text(
                modifier = Modifier.align(Alignment.CenterVertically),
                text = "全ての商品",
                style = MaterialTheme.typography.bodySmall,
            )
        }

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
                placeholder = { Text("商品名またはIDで検索") },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "検索",
                    )
                },
                modifier = Modifier.weight(1f).height(56.dp),
                singleLine = true,
            )

            if (searchText.isNotEmpty()) {
                IconButton(onClick = {
                    searchText = ""
                }) {
                    Icon(
                        imageVector = Icons.Default.Clear,
                        contentDescription = "クリア",
                    )
                }
            }
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

            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                items(filteredProducts.size) { index ->
                    val product = filteredProducts[index]
                    DetailedProductItem(product)
                }
            }
        }
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
                    text = "¥${product.totalRevenue.toString().reversed().chunked(3)
                        .joinToString(",").reversed()}",
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