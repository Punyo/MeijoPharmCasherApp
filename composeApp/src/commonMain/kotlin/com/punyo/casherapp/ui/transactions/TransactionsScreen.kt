package com.punyo.casherapp.ui.transactions

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.PrimaryTabRow
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.aay.compose.barChart.BarChart
import com.aay.compose.barChart.model.BarParameters
import kotlinx.datetime.LocalDateTime
import kotlin.random.Random

// データクラス定義
data class Transaction(
    val id: String,
    val timestamp: LocalDateTime,
    val items: List<TransactionItem>,
    val totalAmount: Int,
    val discount: Float,
    val paymentMethod: PaymentMethod,
)

data class TransactionItem(
    val productId: String,
    val name: String,
    val quantity: Int,
    val unitPrice: Int,
    val totalPrice: Int,
)

data class ProductSummary(
    val productId: String,
    val name: String,
    val image: String?,
    val totalQuantity: Int,
    val totalRevenue: Int,
    val unitPrice: Int,
)

enum class PaymentMethod {
    CASH,
    CARD,
    QR_CODE,
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TransactionsScreen() {
    var selectedTabIndex by remember { mutableStateOf(0) }
    var searchQuery by remember { mutableStateOf("") }
    val mockTransactions = generateMockTransactions()
    val mockProductSummary = generateMockProductSummary()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("取引履歴") },
                actions = {
                    IconButton(onClick = { /* TODO: Filter */ }) {
                        Icon(Icons.Default.FilterList, contentDescription = "フィルター")
                    }
                    IconButton(onClick = { /* TODO: Refresh */ }) {
                        Icon(Icons.Default.Refresh, contentDescription = "更新")
                    }
                },
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { /* TODO: Export */ },
            ) {
                Icon(Icons.Default.Download, contentDescription = "エクスポート")
            }
        },
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
        ) {
            // サマリーカード
            SummaryCardsSection(
                transactions = mockTransactions,
                productSummary = mockProductSummary,
            )

            // タブレイアウト
            PrimaryTabRow(
                selectedTabIndex = selectedTabIndex,
            ) {
                Tab(
                    selected = selectedTabIndex == 0,
                    onClick = { selectedTabIndex = 0 },
                    text = { Text("取引履歴") },
                )
                Tab(
                    selected = selectedTabIndex == 1,
                    onClick = { selectedTabIndex = 1 },
                    text = { Text("商品別集計") },
                )
            }

            // タブコンテンツ
            when (selectedTabIndex) {
                0 -> TransactionHistoryTab(mockTransactions)
                1 -> ProductSummaryTab(mockProductSummary)
            }
        }
    }
}

@Composable
fun SummaryCardsSection(
    transactions: List<Transaction>,
    productSummary: List<ProductSummary>,
) {
    val customerCount = transactions.size
    val totalQuantity = productSummary.sumOf { it.totalQuantity }
    val totalAmount = transactions.sumOf { it.totalAmount }
    val averagePrice = if (totalQuantity > 0) totalAmount / totalQuantity else 0

    LazyRow(
        modifier = Modifier.padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        item { SummaryCard("今日の顧客数", "$customerCount 人") }
        item { SummaryCard("今日の売上個数", "$totalQuantity 個") }
        item {
            SummaryCard(
                "今日の売上金額",
                "¥${totalAmount.toString().reversed().chunked(3).joinToString(",").reversed()}",
            )
        }
    }

    Spacer(modifier = Modifier.height(16.dp))
}

@Composable
fun SummaryCard(
    title: String,
    value: String,
) {
    ElevatedCard(
        modifier = Modifier
            .width(120.dp)
            .height(80.dp),
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(8.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Text(
                text = value,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
            )
        }
    }
}

@Composable
fun TransactionHistoryTab(transactions: List<Transaction>) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        items(transactions) { transaction ->
            TransactionItem(transaction)
        }
    }
}

@Composable
fun TransactionItem(transaction: Transaction) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Text(
                    text = "ID: ${transaction.id}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                Text(
                    text = transaction.paymentMethod.name,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.primary,
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            transaction.items.forEach { item ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                ) {
                    Text(item.name)
                    Text("×${item.quantity}")
                    Text("¥${item.totalPrice}")
                }
            }

            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "合計: ¥${transaction.totalAmount}",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
            )
        }
    }
}

@Composable
fun ProductSummaryTab(productSummary: List<ProductSummary>) {
    LazyVerticalGrid(
        columns = GridCells.Adaptive(minSize = 500.dp),
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        item(span = { GridItemSpan(maxLineSpan) }) {
            ProductSalesBarChart(
                productData = productSummary.take(8),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(400.dp)
                    .padding(vertical = 16.dp),
            )
        }
        items(productSummary) { product ->
            ProductSummaryItem(product)
        }
    }
}

@Composable
fun ProductSummaryItem(product: ProductSummary) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
    ) {
        Column(
            modifier = Modifier
                .fillMaxHeight()
                .padding(16.dp),
            verticalArrangement = Arrangement.SpaceBetween,
        ) {
            // 上部：商品情報
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.Top,
            ) {
                // 商品画像プレースホルダー
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .background(
                            MaterialTheme.colorScheme.surfaceVariant,
                            RoundedCornerShape(4.dp),
                        ),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        text = product.name.first().toString(),
                        style = MaterialTheme.typography.titleMedium,
                    )
                }

                Spacer(modifier = Modifier.width(16.dp))

                Column(
                    modifier = Modifier.weight(1f),
                ) {
                    Text(
                        text = product.name,
                        style = MaterialTheme.typography.titleMedium,
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "売上個数: ${product.totalQuantity}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                    Text(
                        text = "売上金額: ¥${product.totalRevenue}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
                Spacer(modifier = Modifier.weight(1f))
                Text(
                    text = "¥${product.unitPrice}",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                )
            }

//            // 下部：単価（右寄せ）
//            Row(
//                modifier = Modifier.fillMaxWidth(),
//                horizontalArrangement = Arrangement.End,
//            ) {
//                Text(
//                    text = "¥${product.unitPrice}",
//                    style = MaterialTheme.typography.titleMedium,
//                    fontWeight = FontWeight.Bold,
//                )
//            }
        }
    }
}

// モックデータ生成関数
fun generateMockTransactions(): List<Transaction> {
    val products = listOf(
        "感冒薬A",
        "胃腸薬B",
        "湿布C",
        "目薬D",
        "絆創膏E",
    )
    val paymentMethods = PaymentMethod.values()

    return (1..20).map { index ->
        val items = (1..Random.nextInt(1, 4)).map {
            val product = products.random()
            val unitPrice = Random.nextInt(300, 2000)
            val quantity = Random.nextInt(1, 3)
            TransactionItem(
                productId = "P${Random.nextInt(1, 100)}",
                name = product,
                quantity = quantity,
                unitPrice = unitPrice,
                totalPrice = unitPrice * quantity,
            )
        }

        val subtotal = items.sumOf { it.totalPrice }
        val discount = if (Random.nextBoolean()) Random.nextFloat() * 10 else 0f
        val total = (subtotal * (1 - discount / 100)).toInt()

        Transaction(
            id = "T${index.toString().padStart(3, '0')}",
            timestamp = LocalDateTime(2024, 1, 1, Random.nextInt(9, 18), Random.nextInt(0, 60)),
            items = items,
            totalAmount = total,
            discount = discount,
            paymentMethod = paymentMethods.random(),
        )
    }
}

fun generateMockProductSummary(): List<ProductSummary> {
    val products = listOf(
        "感冒薬A" to 800,
        "胃腸薬B" to 1200,
        "湿布C" to 600,
        "目薬D" to 900,
        "絆創膏E" to 300,
        "頭痛薬F" to 700,
        "咳止め薬G" to 1100,
    )

    return products.mapIndexed { index, (name, unitPrice) ->
        val quantity = Random.nextInt(5, 50)
        ProductSummary(
            productId = "P${(index + 1).toString().padStart(3, '0')}",
            name = name,
            image = null,
            totalQuantity = quantity,
            totalRevenue = unitPrice * quantity,
            unitPrice = unitPrice,
        )
    }.sortedByDescending { it.totalRevenue }
}

// AAY-Chart実装
@Composable
fun ProductSalesBarChart(
    productData: List<ProductSummary>,
    modifier: Modifier = Modifier,
) {
    Box(modifier = modifier) {
        val barParameters = BarParameters(
            dataName = "商品売上",
            data = productData.map { it.totalRevenue.toDouble() },
            barColor = MaterialTheme.colorScheme.primary,
        )

        BarChart(
            chartParameters = listOf(barParameters),
            xAxisData = productData.map {
                it.name
            },
            animateChart = true,
            showGridWithSpacer = true,
            yAxisStyle = TextStyle(
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurface,
            ),
            xAxisStyle = TextStyle(
                fontSize = 10.sp,
                color = MaterialTheme.colorScheme.onSurface,
            ),
        )
    }
}
