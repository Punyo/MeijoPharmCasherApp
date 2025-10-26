package com.punyo.casherapp.ui.register

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import casherapp.composeapp.generated.resources.Res
import casherapp.composeapp.generated.resources.button_apply
import casherapp.composeapp.generated.resources.button_cancel
import casherapp.composeapp.generated.resources.button_confirm
import casherapp.composeapp.generated.resources.dialog_discount_apply
import casherapp.composeapp.generated.resources.dialog_quantity_change
import casherapp.composeapp.generated.resources.label_discount_rate
import casherapp.composeapp.generated.resources.label_quantity
import org.jetbrains.compose.resources.stringResource

@Composable
fun QuantityUpdateDialog(
    productName: String,
    currentQuantity: Int,
    onConfirm: (Int) -> Unit,
    onDismiss: () -> Unit,
) {
    var quantityText by remember { mutableStateOf(currentQuantity.toString()) }
    val quantity = quantityText.toIntOrNull() ?: 0

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier.padding(16.dp),
            shape = RoundedCornerShape(16.dp),
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
            ) {
                Text(
                    text = stringResource(Res.string.dialog_quantity_change),
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                )

                Text(
                    text = productName,
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(vertical = 8.dp),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )

                OutlinedTextField(
                    value = quantityText,
                    onValueChange = { quantityText = it },
                    label = { Text(stringResource(Res.string.label_quantity)) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                )

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp),
                    horizontalArrangement = Arrangement.End,
                ) {
                    TextButton(
                        onClick = onDismiss,
                    ) {
                        Text(stringResource(Res.string.button_cancel))
                    }

                    TextButton(
                        onClick = { onConfirm(quantity) },
                        enabled = quantity > 0,
                    ) {
                        Text(stringResource(Res.string.button_confirm))
                    }
                }
            }
        }
    }
}

@Composable
fun DiscountDialog(
    productName: String,
    currentDiscountPercent: Int,
    onConfirm: (Int) -> Unit,
    onDismiss: () -> Unit,
) {
    var discount by remember { mutableStateOf(currentDiscountPercent) }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier.padding(16.dp),
            shape = RoundedCornerShape(16.dp),
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
            ) {
                Text(
                    text = stringResource(Res.string.dialog_discount_apply),
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                )

                Text(
                    text = productName,
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(vertical = 8.dp),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )

                Column(
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Text(
                        text = stringResource(Res.string.label_discount_rate, discount),
                        style = MaterialTheme.typography.bodyLarge,
                        modifier = Modifier.padding(bottom = 8.dp),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )

                    Slider(
                        value = discount.toFloat(),
                        onValueChange = { discount = it.toInt() },
                        valueRange = 0f..100f,
                        steps = 99,
                        modifier = Modifier.fillMaxWidth(),
                    )
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp),
                    horizontalArrangement = Arrangement.End,
                ) {
                    TextButton(
                        onClick = onDismiss,
                    ) {
                        Text(stringResource(Res.string.button_cancel))
                    }

                    TextButton(
                        onClick = { onConfirm(discount) },
                    ) {
                        Text(stringResource(Res.string.button_apply))
                    }
                }
            }
        }
    }
}
