package com.punyo.casherapp.ui.product

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
actual fun ProductDialogBarcodeArea(
    barcode: String,
    onBarcodeChange: (String) -> Unit,
    modifier: Modifier,
) {
    Column(modifier = modifier) {
        OutlinedTextField(
            value = barcode,
            onValueChange = onBarcodeChange,
            label = { Text("二次元コード") },
            placeholder = { Text("カメラで読み取りまたは手入力") },
            trailingIcon = {
//            IconButton(onClick = { showCamera = !showCamera }) {
//                Icon(
//                    imageVector = Icons.Default.CameraAlt,
//                    contentDescription = "カメラで読み取り",
//                )
//            }
            },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
        )
//
//    if (showCamera) {
//        BarcodeScanner(
//            modifier = Modifier
//                .fillMaxWidth(),
//            onResult = {
//                qrCode = it
//                showCamera = false
//            },
//        )
//    }
    }
}
