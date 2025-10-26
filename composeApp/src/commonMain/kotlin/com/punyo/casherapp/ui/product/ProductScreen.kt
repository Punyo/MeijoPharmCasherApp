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
import com.punyo.casherapp.extensions.format
import com.punyo.casherapp.extensions.toLong
import com.punyo.casherapp.ui.component.DataTable
import com.punyo.casherapp.ui.component.TableColumn
import org.koin.compose.koinInject
import kotlin.collections.listOf
import casherapp.composeapp.generated.resources.Res
import casherapp.composeapp.generated.resources.action_delete
import casherapp.composeapp.generated.resources.action_edit
import casherapp.composeapp.generated.resources.barcode_not_registered
import casherapp.composeapp.generated.resources.barcode_registered
import casherapp.composeapp.generated.resources.button_clear_search
import casherapp.composeapp.generated.resources.content_desc_add_product
import casherapp.composeapp.generated.resources.content_desc_clear
import casherapp.composeapp.generated.resources.content_desc_search
import casherapp.composeapp.generated.resources.message_no_products
import casherapp.composeapp.generated.resources.message_no_search_results
import casherapp.composeapp.generated.resources.placeholder_search_product_barcode
import casherapp.composeapp.generated.resources.table_header_barcode
import casherapp.composeapp.generated.resources.table_header_price
import casherapp.composeapp.generated.resources.table_header_product_name
import org.jetbrains.compose.resources.stringResource

@Composable
fun ProductScreen(viewModel: ProductScreenViewModel = koinInject()) {
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
                        listOf<TableColumn<ProductDataModel>>(
                            TableColumn(
                                header = stringResource(Res.string.table_header_product_name),
                                accessor = { it.name },
                                width = 3f,
                            ),
                            TableColumn(
                                header = stringResource(Res.string.table_header_barcode),
                                accessor = {
                                    if (it.barcode == null) {
                                        stringResource(Res.string.barcode_not_registered)
                                    } else {
                                        stringResource(Res.string.barcode_registered)
                                    }
                                },
                                width = 1f,
                            ),
                            TableColumn(
                                header = stringResource(Res.string.table_header_price),
                                accessor = { it.price.format() },
                                width = 1f,
                                isRightAligned = true,
                            ),
                        )

                    DataTable(
                        data = products,
                        columns = productColumns,
                        modifier = Modifier.fillMaxWidth(),
                        actions =
                        mapOf(
                            stringResource(Res.string.action_edit) to { index ->
                                editingProductId = products[index].id
                                editingProductDialogState.productName = products[index].name
                                editingProductDialogState.barcode = products[index].barcode
                                editingProductDialogState.price = products[index].price.toLong()
                                showEditProductDialog = true
                            },
                            stringResource(Res.string.action_delete) to { index ->
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
                contentDescription = stringResource(Res.string.content_desc_add_product),
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
                text = stringResource(Res.string.message_no_products),
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        } else {
            // 検索結果なしの場合
            Text(
                text = stringResource(Res.string.message_no_search_results),
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Spacer(modifier = Modifier.height(16.dp))
            TextButton(
                modifier = Modifier.padding(8.dp),
                onClick = onClearSearch,
            ) {
                Text(
                    text = stringResource(Res.string.button_clear_search),
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
            placeholder = { Text(stringResource(Res.string.placeholder_search_product_barcode)) },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = stringResource(Res.string.content_desc_search),
                )
            },
            trailingIcon = {
                if (searchText.isNotEmpty()) {
                    IconButton(onClick = { onSearchTextChange("") }) {
                        Icon(
                            imageVector = Icons.Default.Clear,
                            contentDescription = stringResource(Res.string.content_desc_clear),
                        )
                    }
                }
            },
            modifier = Modifier.weight(1f).height(56.dp),
            singleLine = true,
        )
    }
}
