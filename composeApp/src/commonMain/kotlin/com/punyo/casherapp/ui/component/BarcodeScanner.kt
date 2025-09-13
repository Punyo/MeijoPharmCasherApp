package com.punyo.casherapp.ui.component

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
expect fun BarcodeScanner(
    modifier: Modifier = Modifier,
    onResult: (String) -> Unit,
)
