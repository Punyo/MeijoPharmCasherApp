package com.punyo.casherapp.ui.product

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.punyo.casherapp.data.product.model.ProductDataModel

@Composable
fun AddProductDialog(
    onDismiss: () -> Unit,
    onSaveClick: (String, String, Int, Int) -> Unit = { _, _, _, _ -> },
    onUpdateClick: (Long, String, String, Int, Int) -> Unit = { _, _, _, _, _ -> },
    editingProduct: ProductDataModel? = null,
) {
    Dialog(
        onDismissRequest = onDismiss,
    ) {
        var productName by remember { mutableStateOf(editingProduct?.name ?: "") }
        var qrCode by remember { mutableStateOf(editingProduct?.barcode ?: "") }
        var price by remember { mutableStateOf(editingProduct?.price?.toString() ?: "") }
        var stock by remember { mutableStateOf(editingProduct?.stock?.toString() ?: "") }
        var showCamera by remember { mutableStateOf(false) }

        val productNameError by remember {
            derivedStateOf { productName.isBlank() }
        }
        val priceError by remember {
            derivedStateOf { price.toIntOrNull()?.let { it < 1 } ?: true }
        }
        val stockError by remember {
            derivedStateOf { stock.toIntOrNull()?.let { it < 0 } ?: true }
        }
        val isFormValid by remember {
            derivedStateOf { !productNameError && !priceError && !stockError }
        }

        Card(
            modifier =
                Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(28.dp),
        ) {
            Column(
                modifier =
                    Modifier.fillMaxSize().padding(24.dp),
            ) {
                Text(
                    text = if (editingProduct != null) "商品を編集" else "商品を追加",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
                )

                Column(
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .weight(1f)
                            .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    OutlinedTextField(
                        value = productName,
                        onValueChange = { productName = it },
                        label = { Text("商品名 *") },
                        placeholder = { Text("商品名を入力してください") },
                        isError = productNameError && productName.isNotEmpty(),
                        supportingText = {
                            if (productNameError && productName.isNotEmpty()) {
                                Text("商品名は必須です")
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                    )

                    OutlinedTextField(
                        value = price,
                        onValueChange = { newValue ->
                            if (newValue.all { it.isDigit() }) {
                                price = newValue
                            }
                        },
                        label = { Text("価格 *") },
                        placeholder = { Text("0") },
                        suffix = { Text("円") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        isError = priceError && price.isNotEmpty(),
                        supportingText = {
                        },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                    )

                    OutlinedTextField(
                        value = stock,
                        onValueChange = { newValue ->
                            if (newValue.all { it.isDigit() }) {
                                stock = newValue
                            }
                        },
                        label = { Text("在庫数 *") },
                        placeholder = { Text("0") },
                        suffix = { Text("個") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        isError = stockError && stock.isNotEmpty(),
                        supportingText = {
                            if (stockError && stock.isNotEmpty()) {
                                Text("0以上の整数を入力してください")
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                    )

                    OutlinedTextField(
                        value = qrCode,
                        onValueChange = { qrCode = it },
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
                        supportingText = {
                            if (productNameError && productName.isNotEmpty()) {
                                Text("商品名は必須です")
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                    )

                    if (showCamera) {
                        Image(
                            imageVector = Icons.Default.Close,
                            contentDescription = "カメラを閉じる",
                            modifier =
                                Modifier
                                    .fillMaxWidth()
                                    .aspectRatio(1f),
                        )
                    }
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
                            if (isFormValid) {
                                if (editingProduct != null) {
                                    onUpdateClick(editingProduct.id, productName, qrCode, price.toInt(), stock.toInt())
                                } else {
                                    onSaveClick(productName, qrCode, price.toInt(), stock.toInt())
                                }
                                onDismiss()
                            }
                        },
                        enabled = isFormValid,
                    ) {
                        Text(
                            text = if (editingProduct != null) "更新" else "保存",
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }
        }
    }
}
