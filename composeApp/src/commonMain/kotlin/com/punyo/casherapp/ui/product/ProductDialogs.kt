package com.punyo.casherapp.ui.product

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog

@Composable
fun AddProductDialog(
    onDismiss: () -> Unit,
    onConfirm: (String, String?, UInt) -> Unit,
    productDialogState: ProductDialogState = rememberProductDialogState(),
) {
    ProductDialog(
        title = "商品の追加",
        onDismiss = onDismiss,
        onConfirm = onConfirm,
        productDialogState = productDialogState,
    )
}

@Composable
fun EditProductDialog(
    onDismiss: () -> Unit,
    onConfirm: (String, String, String?, UInt) -> Unit,
    editingProductId: String,
    productDialogState: ProductDialogState,
) {
    ProductDialog(
        title = "商品の編集",
        onDismiss = onDismiss,
        onConfirm =
        { name, barcode, price ->
            onConfirm(
                editingProductId,
                name,
                barcode,
                price,
            )
        },
        productDialogState = productDialogState,
    )
}

@Composable
private fun ProductDialog(
    title: String,
    onDismiss: () -> Unit,
    onConfirm: (String, String?, UInt) -> Unit,
    productDialogState: ProductDialogState,
) {
    Dialog(
        onDismissRequest = onDismiss,
    ) {
        var productNameEmptyError by remember { mutableStateOf(false) }
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(28.dp),
        ) {
            Column(
                modifier = Modifier.fillMaxSize().padding(24.dp),
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
                )

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    OutlinedTextField(
                        value = productDialogState.productName,
                        onValueChange = {
                            productDialogState.productName = it
                            productNameEmptyError = it.isEmpty()
                        },
                        label = { Text("商品名 *") },
                        placeholder = { Text("商品名を入力してください") },
                        isError = productNameEmptyError,
                        supportingText = {
                            if (productNameEmptyError) {
                                Text("商品名は必須です")
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                    )

                    OutlinedTextField(
                        value = productDialogState.price.toString(),
                        onValueChange = { newValue ->
                            if (newValue.all { it.isDigit() }) {
                                productDialogState.price = newValue.toUIntOrNull() ?: 0u
                            }
                        },
                        label = { Text("価格 *") },
                        suffix = { Text("円") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                    )

                    ProductDialogBarcodeArea(
                        barcode = productDialogState.barcode ?: "",
                        onBarcodeChange = { productDialogState.barcode = it },
                        modifier = Modifier.fillMaxWidth(),
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End,
                ) {
                    TextButton(
                        onClick = onDismiss,
                        modifier = Modifier.padding(end = 16.dp),
                    ) {
                        Text("キャンセル", color = MaterialTheme.colorScheme.primary)
                    }
                    TextButton(
                        onClick = {
                            productNameEmptyError = productDialogState.productName.isEmpty()
                            if (!productNameEmptyError) {
                                onConfirm(
                                    productDialogState.productName,
                                    productDialogState.barcode,
                                    productDialogState.price,
                                )
                                onDismiss()
                            }
                        },
                    ) {
                        Text(
                            text = "保存",
                            color = MaterialTheme.colorScheme.primary,
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun rememberProductDialogState(
    productName: String = "",
    barcode: String? = "",
    price: UInt = 0u,
): ProductDialogState = rememberSaveable(saver = ProductDialogState.Saver) {
    ProductDialogStateImpl(
        initialProductName = productName,
        initialBarcode = barcode,
        initialPrice = price,
    )
}

interface ProductDialogState {
    var productName: String
    var barcode: String?
    var price: UInt

    companion object {
        val Saver: Saver<ProductDialogState, List<Any?>> = Saver(
            save = { state ->
                listOf(
                    state.productName,
                    state.barcode,
                    state.price.toString(),
                )
            },
            restore = { list ->
                ProductDialogStateImpl(
                    initialProductName = list[0] as String,
                    initialBarcode = list[1] as String?,
                    initialPrice = (list[2] as String).toUIntOrNull() ?: 0u,
                )
            },
        )
    }
}

private class ProductDialogStateImpl(
    initialProductName: String,
    initialBarcode: String?,
    initialPrice: UInt,
) : ProductDialogState {
    private var _productName = mutableStateOf(initialProductName)
    private var _barcode = mutableStateOf(initialBarcode)
    private var _price = mutableStateOf(initialPrice)

    override var productName: String
        get() = _productName.value
        set(value) {
            _productName.value = value
        }
    override var barcode: String?
        get() = _barcode.value
        set(value) {
            _barcode.value = value
        }
    override var price: UInt
        get() = _price.value
        set(value) {
            _price.value = value
        }
}
