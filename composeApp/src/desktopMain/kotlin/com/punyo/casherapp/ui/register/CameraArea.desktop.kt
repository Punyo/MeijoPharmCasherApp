package com.punyo.casherapp.ui.register

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import com.github.sarxos.webcam.Webcam
import com.punyo.casherapp.ui.component.BarcodeScanner

@OptIn(ExperimentalMaterial3Api::class)
@Composable
actual fun CameraArea(
    modifier: Modifier,
    onBarcodeScanned: (String) -> Unit,
) {
    var availableWebcams by remember { mutableStateOf(listOf<Webcam>()) }
    var selectedWebcam by remember { mutableStateOf<Webcam?>(null) }

    LaunchedEffect(Unit) {
        availableWebcams = Webcam.getWebcams()
        availableWebcams.forEach {
            if (it.isOpen) it.close()
        }
        if (availableWebcams.isNotEmpty() && selectedWebcam == null) {
            selectedWebcam = availableWebcams.first()
        }
    }

    DisposableEffect(Unit) {
        onDispose {
            availableWebcams.forEach { webcam ->
                try {
                    if (webcam.isOpen) {
                        webcam.close()
                    }
                } catch (_: Exception) {
                    // Ignore exceptions during cleanup
                }
            }
        }
    }

    if (availableWebcams.isEmpty()) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.surfaceVariant),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = "利用可能なWebcamが見つかりません",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    } else {
        Column(
            modifier = Modifier.fillMaxSize(),
        ) {
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

            Spacer(modifier = Modifier.height(8.dp))

            selectedWebcam?.let { webcam ->
                key(webcam) {
                    BarcodeScanner(
                        webcam = webcam,
                        modifier = Modifier.fillMaxSize().zIndex(-1f),
                        onResult = onBarcodeScanned,
                    )
                }
            }
        }
    }
}
