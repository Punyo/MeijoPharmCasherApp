package com.punyo.casherapp.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyGridScope
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import kotlin.math.min

@Composable
fun ResponsiveGrid(
    modifier: Modifier = Modifier,
    minCellWidth: Dp = 320.dp,
    maxColumns: Int = 2,
    contentPadding: PaddingValues = PaddingValues(16.dp),
    verticalSpacing: Dp = 16.dp,
    horizontalSpacing: Dp = 16.dp,
    content: LazyGridScope.() -> Unit,
) {
    BoxWithConstraints(modifier = modifier.fillMaxSize()) {
        val horizontalPadding = contentPadding.calculateLeftPadding(LayoutDirection.Ltr) +
            contentPadding.calculateRightPadding(LayoutDirection.Ltr)
        val interItemSpacing = horizontalSpacing

        val availableColumns = (
            (maxWidth - horizontalPadding + interItemSpacing) /
                (minCellWidth + interItemSpacing)
            ).toInt()

        val gridColumns = min(maxColumns, availableColumns).coerceAtLeast(1)

        LazyVerticalGrid(
            columns = GridCells.Fixed(gridColumns),
            modifier = Modifier.fillMaxSize(),
            contentPadding = contentPadding,
            verticalArrangement = Arrangement.spacedBy(verticalSpacing),
            horizontalArrangement = Arrangement.spacedBy(horizontalSpacing),
            content = content,
        )
    }
}
