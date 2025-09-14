package com.punyo.casherapp.ui.product

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun ProductDialogBarcodeAreaCommonTextField(
    barcode: String,
    onBarcodeChange: (String) -> Unit,
    onCameraClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    OutlinedTextField(
        value = barcode,
        onValueChange = onBarcodeChange,
        label = { Text("二次元コード") },
        placeholder = { Text("カメラで読み取りまたは手入力") },
        trailingIcon = {
            IconButton(onClick = onCameraClick) {
                Icon(
                    imageVector = Icons.Default.CameraAlt,
                    contentDescription = null,
                )
            }
        },
        supportingText = {},
        modifier = modifier.fillMaxWidth(),
        singleLine = true,
    )
}
