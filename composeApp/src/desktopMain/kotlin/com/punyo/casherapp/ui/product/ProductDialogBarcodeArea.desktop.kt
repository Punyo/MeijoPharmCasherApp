package com.punyo.casherapp.ui.product

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.punyo.casherapp.ui.component.BarcodeScanner

@Composable
actual fun ProductDialogBarcodeArea(
    barcode: String,
    onBarcodeChange: (String) -> Unit,
    modifier: Modifier,
) {
    var showCamera by remember { mutableStateOf(false) }
    Column(modifier = modifier) {
        OutlinedTextField(
            value = barcode,
            onValueChange = onBarcodeChange,
            label = { Text("二次元コード") },
            placeholder = { Text("カメラで読み取りまたは手入力") },
            trailingIcon = {
                IconButton(onClick = { showCamera = !showCamera }) {
                    Icon(
                        imageVector = Icons.Default.CameraAlt,
                        contentDescription = "カメラで読み取り",
                    )
                }
            },
            supportingText = {},
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
        )

        if (showCamera) {
            BarcodeScanner(
                modifier = Modifier
                    .fillMaxWidth(),
                onResult = {
                    onBarcodeChange(it)
                    showCamera = false
                },
            )
        }
    }
}
