package com.punyo.casherapp.ui.product

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SuggestionChip
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

// 仮のデータ型
data class Product(
    val id: String,
    val name: String,
    val barcode: String,
    val price: Int,
    val category: String,
    val stock: Int,
)

@Composable
fun ProductScreen() {
    var searchText by remember { mutableStateOf("") }
    var showAddProductDialog by rememberSaveable { mutableStateOf(false) }

    // 仮の商品データ（空リスト）
    val products = remember { emptyList<Product>() }

    Box(
        modifier = Modifier.fillMaxSize(),
    ) {
        Column(
            modifier =
                Modifier
                    .fillMaxSize()
                    .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            // ヘッダー部分
            ProductHeader(
                searchText = searchText,
                onSearchTextChange = { searchText = it },
                onBarcodeClick = { /* TODO: バーコードスキャン */ },
                onMenuClick = { /* TODO: メニュー表示 */ },
            )

            // 商品一覧エリア
            ProductList(
                products = products,
                searchText = searchText,
                onProductClick = { /* TODO: 商品詳細画面への遷移 */ },
                onProductMenuClick = { /* TODO: 商品メニュー表示 */ },
            )
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
//        AddProductDialog(
//            onDismiss = { showAddProductDialog = false },
//            onSaveClick = { name, qr, price, stock ->
//                // TODO: 商品保存処理
//            },
//        )
    }
}

@Composable
private fun ProductList(
    products: List<Product>,
    searchText: String,
    onProductClick: (Product) -> Unit,
    onProductMenuClick: (Product) -> Unit,
) {
    if (products.isEmpty()) {
        // 空状態表示
        EmptyState(
            searchText = searchText,
            onClearSearch = { /* TODO: 検索クリア */ },
        )
    } else {
        LazyColumn(
            contentPadding = PaddingValues(vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(1.dp),
        ) {
            items(products) { product ->
                ProductItem(
                    product = product,
                    onClick = { onProductClick(product) },
                    onMenuClick = { onProductMenuClick(product) },
                )
            }
        }
    }
}

@Composable
private fun ProductItem(
    product: Product,
    onClick: () -> Unit,
    onMenuClick: () -> Unit,
) {
    Card(
        onClick = onClick,
        modifier =
            Modifier
                .fillMaxWidth()
                .height(72.dp),
    ) {
        Row(
            modifier =
                Modifier
                    .fillMaxSize()
                    .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            // 商品情報部分
            Column(
                modifier = Modifier.weight(1f),
            ) {
                Text(
                    text = product.name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                )
                Text(
                    text = "バーコード: ${product.barcode}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                SuggestionChip(
                    onClick = { },
                    label = { Text(product.category) },
                )
            }

            // 価格・在庫情報部分
            Column(
                horizontalAlignment = Alignment.End,
            ) {
                Text(
                    text = "¥${product.price}",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                )
                Text(
                    text = "在庫:${product.stock}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }

            Spacer(modifier = Modifier.width(8.dp))

            // メニューボタン
            IconButton(onClick = onMenuClick) {
                Icon(
                    imageVector = Icons.Default.MoreVert,
                    contentDescription = "メニュー",
                )
            }
        }
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
            Text(
                text = "検索条件をクリア",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.primary,
                modifier =
                    Modifier
                        .padding(8.dp),
            )
        }
    }
}

@Composable
private fun ProductHeader(
    searchText: String,
    onSearchTextChange: (String) -> Unit,
    onBarcodeClick: () -> Unit,
    onMenuClick: () -> Unit,
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

        IconButton(onClick = onBarcodeClick) {
            Icon(
                imageVector = Icons.Default.CameraAlt,
                contentDescription = "バーコードスキャン",
            )
        }

        IconButton(onClick = onMenuClick) {
            Icon(
                imageVector = Icons.Default.MoreVert,
                contentDescription = "メニュー",
            )
        }
    }
}
