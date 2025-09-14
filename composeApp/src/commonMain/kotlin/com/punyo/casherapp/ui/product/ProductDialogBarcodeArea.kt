package com.punyo.casherapp.ui.product

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
expect fun ProductDialogBarcodeArea(
    barcode: String,
    onBarcodeChange: (String) -> Unit,
    modifier: Modifier = Modifier,
)
