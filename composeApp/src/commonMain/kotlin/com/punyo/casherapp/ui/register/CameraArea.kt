package com.punyo.casherapp.ui.register

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
expect fun CameraArea(
    modifier: Modifier = Modifier,
    onBarcodeScanned: (String) -> Unit,
)
