package com.punyo.casherapp.ui.product

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.punyo.casherapp.data.product.model.ProductDataModel
import com.punyo.casherapp.ui.component.DataTable
import com.punyo.casherapp.ui.component.TableColumn
import org.koin.compose.koinInject
import kotlin.collections.listOf

@Composable
fun ProductScreen(viewModel: ProductViewModel = koinInject()) {
    val uiState by viewModel.uiState.collectAsState()
    var showAddProductDialog by remember { mutableStateOf(false) }
    var showEditProductDialog by remember { mutableStateOf(false) }
    var editingProductId by remember { mutableStateOf("") }

    val editingProductDialogState = rememberProductDialogState()

    Box(
        modifier = Modifier.fillMaxSize(),
    ) {
        Column(
            modifier =
            Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            ProductHeader(
                searchText = uiState.searchText,
                onSearchTextChange = viewModel::updateSearchText,
            )
            if (uiState.products != null) {
                val products = uiState.filteredProducts!!
                if (products.isEmpty()) {
                    EmptyState(
                        searchText = uiState.searchText,
                        onClearSearch = viewModel::clearSearch,
                    )
                } else {
                    val productColumns =
                        remember {
                            listOf<TableColumn<ProductDataModel>>(
                                TableColumn(
                                    header = "商品名",
                                    accessor = { it.name },
                                    width = 3f,
                                ),
                                TableColumn(
                                    header = "二次元コード",
                                    accessor = {
                                        if (it.barcode == null) {
                                            "未登録"
                                        } else {
                                            "登録済み"
                                        }
                                    },
                                    width = 1f,
                                ),
                                TableColumn(
                                    header = "価格",
                                    accessor = { it.price },
                                    width = 1f,
                                    isRightAligned = true,
                                ),
                            )
                        }

                    DataTable(
                        data = products,
                        columns = productColumns,
                        modifier = Modifier.fillMaxWidth(),
                        actions =
                        mapOf(
                            "編集" to { index ->
                                editingProductId = products[index].id
                                editingProductDialogState.productName = products[index].name
                                editingProductDialogState.barcode = products[index].barcode
                                editingProductDialogState.price = products[index].price.toUInt()
                                showEditProductDialog = true
                            },
                            "削除" to { index ->
                                viewModel.deleteProduct(products[index].id)
                            },
                        ),
                    )
                }
            } else {
                CircularProgressIndicator(
                    modifier = Modifier
                        .fillMaxSize()
                        .wrapContentWidth(Alignment.CenterHorizontally)
                        .padding(32.dp),
                )
            }
        }

        FloatingActionButton(
            onClick = { showAddProductDialog = true },
            modifier =
            Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp),
        ) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = "商品を追加",
            )
        }
    }

    if (showAddProductDialog) {
        AddProductDialog(
            onDismiss = { showAddProductDialog = false },
            onConfirm = { name, barcode, price ->
                viewModel.addProduct(name, barcode, price)
                showAddProductDialog = false
            },
        )
    }

    if (showEditProductDialog) {
        EditProductDialog(
            onDismiss = { showEditProductDialog = false },
            onConfirm = { id, name, barcode, price ->
                viewModel.updateProduct(id, name, barcode, price)
                showEditProductDialog = false
            },
            editingProductId = editingProductId,
            productDialogState = editingProductDialogState,
        )
    }
}

@Composable
private fun EmptyState(
    searchText: String,
    onClearSearch: () -> Unit,
) {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        if (searchText.isEmpty()) {
            // 商品なしの場合
            Text(
                text = "商品を追加してください",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        } else {
            // 検索結果なしの場合
            Text(
                text = "該当する商品が見つかりません",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Spacer(modifier = Modifier.height(16.dp))
            TextButton(
                modifier = Modifier.padding(8.dp),
                onClick = onClearSearch,
            ) {
                Text(
                    text = "検索条件をクリア",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.primary,
                )
            }
        }
    }
}

@Composable
private fun ProductHeader(
    searchText: String,
    onSearchTextChange: (String) -> Unit,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        OutlinedTextField(
            value = searchText,
            onValueChange = onSearchTextChange,
            placeholder = { Text("商品名またはバーコードで検索") },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = "検索",
                )
            },
            trailingIcon = {
                if (searchText.isNotEmpty()) {
                    IconButton(onClick = { onSearchTextChange("") }) {
                        Icon(
                            imageVector = Icons.Default.Clear,
                            contentDescription = "クリア",
                        )
                    }
                }
            },
            modifier = Modifier.weight(1f).height(56.dp),
            singleLine = true,
        )
    }
}
