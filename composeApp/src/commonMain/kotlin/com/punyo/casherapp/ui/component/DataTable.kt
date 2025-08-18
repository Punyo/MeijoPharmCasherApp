package com.punyo.casherapp.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

enum class SortDirection {
    ASC,
    DESC,
}

data class SortState(
    val columnIndex: Int,
    val direction: SortDirection,
)

data class TableColumn<T>(
    val header: String,
    val accessor: (T) -> Any?,
    val width: Float = 1f,
)

@Suppress("UNCHECKED_CAST")
@Composable
fun <T> DataTable(
    data: List<T>,
    columns: List<TableColumn<T>>,
    modifier: Modifier = Modifier,
    actions: Map<String, (Int) -> Unit> = emptyMap(),
) {
    var sortState by remember { mutableStateOf<SortState?>(null) }
    var expandedMenuIndex by remember { mutableStateOf<Int?>(null) }

    val sortedData =
        remember(data, sortState) {
            if (sortState == null) {
                data
            } else {
                val column = columns[sortState!!.columnIndex]

                val sortKey = column.accessor as? (T) -> Comparable<*>?
                if (sortKey != null) {
                    when (sortState!!.direction) {
                        SortDirection.ASC ->
                            data.sortedWith { a, b ->
                                val aValue = sortKey(a)
                                val bValue = sortKey(b)
                                compareValues(aValue, bValue)
                            }

                        SortDirection.DESC ->
                            data.sortedWith { a, b ->
                                val aValue = sortKey(a)
                                val bValue = sortKey(b)
                                compareValues(bValue, aValue)
                            }
                    }
                } else {
                    null
                }
            }
        }

    val onSort: (Int) -> Unit = { columnIndex ->
        sortState =
            if (sortState?.columnIndex == columnIndex) {
                // 同じ列をクリックした場合は方向を切り替え
                when (sortState!!.direction) {
                    SortDirection.ASC -> SortState(columnIndex, SortDirection.DESC)
                    SortDirection.DESC -> null // ソートをクリア
                }
            } else {
                // 異なる列をクリックした場合は昇順でソート
                SortState(columnIndex, SortDirection.ASC)
            }
    }
    LazyColumn(modifier = modifier) {
        stickyHeader {
            Row(
                modifier =
                    Modifier
                        .height(32.dp)
                        .padding(horizontal = 16.dp)
                        .background(MaterialTheme.colorScheme.surface),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                columns.forEachIndexed { index, column ->
                    Row(
                        modifier =
                            Modifier
                                .fillMaxHeight()
                                .weight(column.width)
                                .let { modifier ->
                                    if (sortedData != null) {
                                        modifier.clickable(
                                            indication = null,
                                            interactionSource = remember { MutableInteractionSource() },
                                        ) { onSort(index) }
                                    } else {
                                        modifier
                                    }
                                },
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                    ) {
                        Text(
                            text = column.header,
                            style = MaterialTheme.typography.titleSmall,
                            modifier = Modifier.weight(1f),
                        )

                        if (sortedData != null && sortState?.columnIndex == index) {
                            Icon(
                                imageVector =
                                    when (sortState!!.direction) {
                                        SortDirection.ASC -> Icons.Default.KeyboardArrowUp
                                        SortDirection.DESC -> Icons.Default.KeyboardArrowDown
                                    },
                                contentDescription = "Sort ${sortState!!.direction.name}",
                            )
                        }
                    }
                }

                if (actions.isNotEmpty()) {
                    Box(
                        modifier = Modifier.width(48.dp).fillMaxHeight(),
                    )
                }
            }
            HorizontalDivider()
        }

        itemsIndexed(sortedData ?: data) { displayIndex, item ->
            val originalIndex = data.indexOf(item)

            Row(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .clickable(
                            onClick = { },
                        ).padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                columns.forEach { column ->
                    val value = column.accessor(item)
                    Text(
                        text = value?.toString() ?: "",
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.weight(column.width),
                    )
                }

                if (actions.isNotEmpty()) {
                    Box(
                        modifier = Modifier.height(48.dp).aspectRatio(1f),
                    ) {
                        IconButton(
                            onClick = { expandedMenuIndex = displayIndex },
                        ) {
                            Icon(
                                imageVector = Icons.Default.MoreVert,
                                contentDescription = "Menu",
                            )
                        }

                        DropdownMenu(
                            expanded = expandedMenuIndex == displayIndex,
                            onDismissRequest = { expandedMenuIndex = null },
                        ) {
                            actions.forEach { (actionName, actionCallback) ->
                                DropdownMenuItem(
                                    text = { Text(actionName) },
                                    onClick = {
                                        actionCallback(originalIndex)
                                        expandedMenuIndex = null
                                    },
                                )
                            }
                        }
                    }
                }
            }

            if (displayIndex < (sortedData ?: data).lastIndex) {
                HorizontalDivider()
            }
        }
    }
}
