package com.punyo.casherapp.ui.transactions

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowRightAlt
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
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

enum class TimePeriod(val displayName: String) {
    TODAY("今日"),
    ALL_TIME("全体"),
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TransactionsScreen() {
    var selectedPeriod by remember { mutableStateOf(TimePeriod.TODAY) }
    val mockTransactions = generateMockTransactions()
    val mockProductSummary = generateMockProductSummary()

    // 全期間データの生成（モック）
    val allTimeTransactions = generateMockTransactions(multiplier = 30) // 30日分のデータ
    val allTimeProductSummary = generateMockProductSummary(multiplier = 30)

    // 選択された期間に応じてデータを切り替え
    val currentTransactions = when (selectedPeriod) {
        TimePeriod.TODAY -> mockTransactions
        TimePeriod.ALL_TIME -> allTimeTransactions
    }

    val currentProductSummary = when (selectedPeriod) {
        TimePeriod.TODAY -> mockProductSummary
        TimePeriod.ALL_TIME -> allTimeProductSummary
    }

    val customerCount = currentTransactions.size
    val totalQuantity = currentProductSummary.sumOf { it.totalQuantity }
    val totalAmount = currentTransactions.sumOf { it.totalAmount }

    LazyVerticalGrid(
        columns = GridCells.Adaptive(minSize = 320.dp),
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        item(span = { GridItemSpan(maxLineSpan) }) {
            // 期間切り替えボタン
            TimePeriodSelector(
                selectedPeriod = selectedPeriod,
                onPeriodSelected = {
                    selectedPeriod = it
                },
            )
        }

        item(span = { GridItemSpan(maxLineSpan) }) {
            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                EnhancedSummaryCard(
                    modifier = Modifier.weight(1f),
                    title = "今日の顧客数",
                    value = "$customerCount 人",
                    icon = Icons.Default.People,
                )
                EnhancedSummaryCard(
                    modifier = Modifier.weight(1f),
                    title = "売上個数",
                    value = "$totalQuantity 個",
                    icon = Icons.Default.ShoppingCart,
                )
                EnhancedSummaryCard(
                    modifier = Modifier.weight(1f),
                    title = "売上金額",
                    value = "¥${totalAmount.toString().reversed().chunked(3).joinToString(",").reversed()}",
                    icon = Icons.Default.AttachMoney,
                )
            }
        }

        item(span = { GridItemSpan(maxLineSpan) }) {
            ProductSalesBarChart(
                productData = currentProductSummary.take(8),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(400.dp),
            )
        }

        item(span = { GridItemSpan(if (maxLineSpan >= 2) maxLineSpan / 2 else maxLineSpan) }) {
            DataListCard(
                title = "最近の取引",
                items = currentTransactions.take(5),
                itemContent = { transaction ->
                    CompactTransactionItem(transaction)
                },
            )
        }

        item(span = { GridItemSpan(if (maxLineSpan >= 2) maxLineSpan / 2 else maxLineSpan) }) {
            DataListCard(
                title = "人気商品",
                items = currentProductSummary.take(5),
                itemContent = { product ->
                    CompactProductItem(product)
                },
            )
        }
    }
}

@Composable
fun TimePeriodSelector(
    selectedPeriod: TimePeriod,
    onPeriodSelected: (TimePeriod) -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Start,
    ) {
        TimePeriod.entries.forEach { period ->
            FilterChip(
                selected = selectedPeriod == period,
                onClick = { onPeriodSelected(period) },
                label = {
                    Text(
                        text = period.displayName,
                        style = MaterialTheme.typography.bodyMedium,
                    )
                },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                    selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer,
                ),
                modifier = Modifier.padding(end = 8.dp),
            )
        }
    }
}

@Composable
fun EnhancedSummaryCard(
    modifier: Modifier = Modifier,
    title: String,
    value: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
) {
    ElevatedCard(
        modifier = modifier
            .height(72.dp),
    ) {
        Row(
            modifier = modifier
                .fillMaxSize()
                .padding(16.dp),
        ) {
            Column(
                modifier = modifier.fillMaxWidth(),
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.weight(1f),
                )
                Text(
                    text = value,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                )
            }
            Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier.fillMaxHeight().aspectRatio(1f),
            )
        }
    }
}

fun generateMockTransactions(multiplier: Int = 1): List<Transaction> {
    val products = listOf(
        "感冒薬A",
        "胃腸薬B",
        "湿布C",
        "目薬D",
        "絆創膏E",
    )
    val paymentMethods = PaymentMethod.entries.toTypedArray()

    return (1..(20 * multiplier)).map { index ->
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

fun generateMockProductSummary(multiplier: Int = 1): List<ProductSummary> {
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
        val quantity = Random.nextInt(5 * multiplier, 50 * multiplier)
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

@Composable
fun <T> DataListCard(
    modifier: Modifier = Modifier,
    title: String,
    items: List<T>,
    itemContent: @Composable (T) -> Unit,
    onViewMoreClick: () -> Unit = {},
) {
    OutlinedCard(
        modifier = modifier
            .fillMaxWidth(),
    ) {
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(16.dp),
        ) {
            Row(
                modifier = modifier.fillMaxWidth().height(IntrinsicSize.Min).padding(bottom = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Text(
                    modifier = Modifier.align(Alignment.CenterVertically),
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                )
                if (onViewMoreClick != {}) {
                    FilledTonalIconButton(
                        modifier = Modifier.height(40.dp).width(40.dp),
                        onClick = onViewMoreClick,
                        shape = CircleShape,
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowRightAlt,
                            contentDescription = "もっと見る",
                            tint = MaterialTheme.colorScheme.onSurface,
                        )
                    }
                }
            }

            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                items.forEach { item ->
                    itemContent(item)
                }
            }
        }
    }
}

@Composable
fun CompactTransactionItem(transaction: Transaction) {
    Card(
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f),
        ),
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = transaction.id,
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.Medium,
                )
                Text(
                    text = "¥${transaction.totalAmount}",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary,
                )
            }
            Text(
                text = "${transaction.items.size}点 | ${transaction.paymentMethod.name}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}

@Composable
fun CompactProductItem(product: ProductSummary) {
    Card(
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f),
        ),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .background(
                        MaterialTheme.colorScheme.primary.copy(alpha = 0.2f),
                        RoundedCornerShape(4.dp),
                    ),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = product.name.first().toString(),
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary,
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = product.name,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium,
                )
                Text(
                    text = "${product.totalQuantity}個売上",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }

            Text(
                text = "¥${product.totalRevenue}",
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.secondary,
            )
        }
    }
}

@Composable
fun ProductSalesBarChart(
    productData: List<ProductSummary>,
    modifier: Modifier = Modifier,
) {
    OutlinedCard(
        modifier = modifier,
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
        ) {
            Text(
                text = "商品売上チャート",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 12.dp),
            )

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(400.dp),
            ) {
                val barParameters = BarParameters(
                    dataName = "商品売上",
                    data = productData.map { it.totalRevenue.toDouble() },
                    barColor = MaterialTheme.colorScheme.primary,
                )

                BarChart(
                    chartParameters = listOf(barParameters),
                    xAxisData = productData.map {
                        if (it.name.length > 6) it.name.take(6) + "..." else it.name
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
    }
}
