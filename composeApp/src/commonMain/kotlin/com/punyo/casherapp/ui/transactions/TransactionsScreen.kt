package com.punyo.casherapp.ui.transactions

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
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
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowRightAlt
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.aay.compose.barChart.BarChart
import com.aay.compose.barChart.model.BarParameters
import com.aay.compose.baseComponents.model.LegendPosition
import com.punyo.casherapp.data.transaction.model.TransactionDataModel
import com.punyo.casherapp.extensions.format
import com.punyo.casherapp.extensions.toDateString
import com.punyo.casherapp.extensions.toDouble
import com.punyo.casherapp.ui.component.ResponsiveGrid
import org.koin.compose.koinInject

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TransactionsScreen(
    viewModel: TransactionsScreenViewModel = koinInject(),
    navController: androidx.navigation.NavController? = null,
) {
    val uiState by viewModel.uiState.collectAsState()
    if (uiState.collectedTransactions != null && uiState.productSummaries != null) {
        TransactionsScreenContent(
            currentPeriod = uiState.currentPeriod,
            productSummaries = uiState.productSummaries!!,
            currentTransactions = viewModel.getTransactionsByPeriod(uiState.currentPeriod),
            customerCount = viewModel.getCustomerCountByPeriod(uiState.currentPeriod),
            totalQuantity = viewModel.getTotalQuantityByPeriod(uiState.currentPeriod),
            totalRevenue = viewModel.getTotalRevenueByPeriod(uiState.currentPeriod),
            onPeriodSelected = { viewModel.setCurrentPeriod(it) },
            onNavigateToAllTransactions = { navController?.navigateToAllTransactions(uiState.currentPeriod) },
            onNavigateToProductsList = { navController?.navigateToProductsList(uiState.currentPeriod) },
            modifier = Modifier.fillMaxSize().padding(8.dp),
        )
    } else {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center,
        ) {
            CircularProgressIndicator(
                modifier = Modifier.width(64.dp).aspectRatio(1f),
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
                        text = when (period) {
                            TimePeriod.TODAY -> "今日"
                            TimePeriod.ALL_TIME -> "全期間"
                        },
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
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                Text(
                    text = value,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
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
            .height(420.dp)
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
fun CompactTransactionItem(transaction: TransactionDataModel) {
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
                    text = transaction.createdAt.toDateString(),
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.Medium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                Text(
                    text = transaction.totalAmount.format(),
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }
            Text(
                text = "${transaction.totalQuantity}点 | ${transaction.id}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
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
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                Text(
                    text = "${product.totalQuantity}個売上",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }

            Text(
                text = product.totalRevenue.format(),
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.secondary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
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
                    .fillMaxWidth(),
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
                    isShowGrid = true,
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
                    legendPosition = LegendPosition.DISAPPEAR,
                )
            }
        }
    }
}

@Composable
private fun TransactionsScreenContent(
    currentPeriod: TimePeriod,
    productSummaries: List<ProductSummary>,
    currentTransactions: List<TransactionDataModel>,
    customerCount: Int,
    totalQuantity: Int,
    totalRevenue: org.joda.money.Money,
    onPeriodSelected: (TimePeriod) -> Unit,
    onNavigateToAllTransactions: () -> Unit,
    onNavigateToProductsList: () -> Unit,
    modifier: Modifier = Modifier,
) {
    ResponsiveGrid(modifier = modifier) { _, _, _ ->
        item(span = { GridItemSpan(maxLineSpan) }) {
            TimePeriodSelector(
                selectedPeriod = currentPeriod,
                onPeriodSelected = onPeriodSelected,
            )
        }
        item(span = { GridItemSpan(maxLineSpan) }) {
            SummaryCardsRow(
                customerCount = customerCount,
                totalQuantity = totalQuantity,
                totalRevenue = totalRevenue,
            )
        }

        item(span = { GridItemSpan(maxLineSpan) }) {
            ProductSalesBarChart(
                productData = productSummaries,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(420.dp),
            )
        }

        item(span = { GridItemSpan(1) }) {
            DataListCard(
                title = "取引",
                items = currentTransactions.take(5),
                itemContent = { transaction ->
                    CompactTransactionItem(transaction)
                },
                onViewMoreClick = onNavigateToAllTransactions,
            )
        }

        item(span = { GridItemSpan(1) }) {
            DataListCard(
                title = "人気商品",
                items = productSummaries.take(5),
                itemContent = { product ->
                    CompactProductItem(product)
                },
                onViewMoreClick = onNavigateToProductsList,
            )
        }
    }
}

@Composable
private fun SummaryCardsRow(
    customerCount: Int,
    totalQuantity: Int,
    totalRevenue: org.joda.money.Money,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        EnhancedSummaryCard(
            modifier = Modifier.weight(1f),
            title = "顧客数",
            value = customerCount.toString(),
            icon = Icons.Default.People,
        )
        EnhancedSummaryCard(
            modifier = Modifier.weight(1f),
            title = "売上個数",
            value = totalQuantity.toString(),
            icon = Icons.Default.ShoppingCart,
        )
        EnhancedSummaryCard(
            modifier = Modifier.weight(1f),
            title = "売上金額",
            value = totalRevenue.format(),
            icon = Icons.Default.AttachMoney,
        )
    }
}
