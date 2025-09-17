package com.punyo.casherapp.ui.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyGridScope
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlin.math.min

@Composable
fun ResponsiveGrid(
    modifier: Modifier = Modifier,
    minCellWidth: Dp = 320.dp,
    maxColumns: Int = 2,
    verticalSpacing: Dp = 16.dp,
    horizontalSpacing: Dp = 16.dp,
    content: LazyGridScope.(maxWidth: Dp, maxHeight: Dp, gridColumns: Int) -> Unit,
) {
    BoxWithConstraints(modifier = modifier.fillMaxSize()) {
        val availableColumns = (
            (maxWidth + horizontalSpacing) /
                (minCellWidth + horizontalSpacing)
            ).toInt()

        val gridColumns = min(maxColumns, availableColumns).coerceAtLeast(1)

        LazyVerticalGrid(
            columns = GridCells.Fixed(gridColumns),
            modifier = Modifier.height(maxHeight).width(maxWidth),
            verticalArrangement = Arrangement.spacedBy(verticalSpacing),
            horizontalArrangement = Arrangement.spacedBy(horizontalSpacing),
        ) {
            content(
                maxWidth,
                maxHeight,
                gridColumns,
            )
        }
    }
}
