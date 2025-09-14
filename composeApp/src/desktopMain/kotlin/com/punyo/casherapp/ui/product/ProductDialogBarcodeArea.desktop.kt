package com.punyo.casherapp.ui.product

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.zIndex
import com.github.sarxos.webcam.Webcam
import com.punyo.casherapp.ui.component.BarcodeScanner

@OptIn(ExperimentalMaterial3Api::class)
@Composable
actual fun ProductDialogBarcodeArea(
    barcode: String,
    onBarcodeChange: (String) -> Unit,
    modifier: Modifier,
) {
    var showCamera by remember { mutableStateOf(false) }
    var availableWebcams by remember { mutableStateOf(listOf<Webcam>()) }
    var selectedWebcam by remember {
        mutableStateOf<Webcam?>(null)
    }
    LaunchedEffect(Unit) {
        availableWebcams = Webcam.getWebcams()
        availableWebcams.forEach {
            it.close()
        }
        if (availableWebcams.isNotEmpty() && selectedWebcam == null) {
            selectedWebcam = availableWebcams.first()
        }
    }
    Column(modifier = modifier) {
        ProductDialogBarcodeAreaCommonTextField(
            barcode = barcode,
            onBarcodeChange = onBarcodeChange,
            onCameraClick = { showCamera = !showCamera },
            modifier = Modifier.fillMaxWidth(),
        )

        if (showCamera) {
            if (availableWebcams.isNotEmpty()) {
                var expanded by remember { mutableStateOf(false) }

                ExposedDropdownMenuBox(
                    expanded = expanded,
                    onExpandedChange = { expanded = !expanded },
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    OutlinedTextField(
                        value = selectedWebcam?.name ?: "Webcamを選択",
                        onValueChange = { },
                        label = { Text("Webcam選択") },
                        trailingIcon = {
                            ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
                        },
                        readOnly = true,
                        modifier = Modifier
                            .menuAnchor(
                                type = MenuAnchorType.PrimaryNotEditable,
                                enabled = true,
                            )
                            .fillMaxWidth(),
                    )
                    ExposedDropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false },
                    ) {
                        availableWebcams.forEach { webcam ->
                            DropdownMenuItem(
                                text = { Text(webcam.name) },
                                onClick = {
                                    selectedWebcam = webcam
                                    expanded = false
                                },
                            )
                        }
                    }
                }

                selectedWebcam?.let { webcam ->
                    BarcodeScanner(
                        webcam = webcam,
                        modifier = Modifier.fillMaxWidth().zIndex(-1f),
                        onResult = {
                            onBarcodeChange(it)
                            showCamera = false
                        },
                    )
                }
            }
        }
    }
}
