package com.punyo.casherapp.ui.product

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

data class ProductUiState(
    val searchText: String = "",
    val products: List<Product> = emptyList(),
    val showAddProductDialog: Boolean = false,
    val isLoading: Boolean = false,
) {
    val filteredProducts: List<Product> =
        if (searchText.isBlank()) {
            products
        } else {
            products.filter { product ->
                product.name.contains(searchText, ignoreCase = true) ||
                    product.barcode.contains(searchText, ignoreCase = true)
            }
        }
}

class ProductViewModel : ViewModel() {
    private val state = MutableStateFlow(ProductUiState())
    val uiState: StateFlow<ProductUiState> = state.asStateFlow()

    fun updateSearchText(text: String) {
        state.value = state.value.copy(searchText = text)
    }

    fun clearSearch() {
        state.value = state.value.copy(searchText = "")
    }

    fun showAddProductDialog() {
        state.value = state.value.copy(showAddProductDialog = true)
    }

    fun hideAddProductDialog() {
        state.value = state.value.copy(showAddProductDialog = false)
    }

    @OptIn(ExperimentalUuidApi::class)
    fun addProduct(
        name: String,
        barcode: String,
        price: Int,
        stock: Int,
    ) {
        val newProduct =
            Product(
                id = Uuid.random().toString(),
                name = name,
                barcode = barcode,
                price = price,
                stock = stock,
            )
        state.value =
            state.value.copy(
                products = state.value.products + newProduct,
            )
        hideAddProductDialog()
    }

    fun onProductClick(product: Product) {
        // TODO: 商品詳細画面への遷移
    }

    fun onProductMenuClick(product: Product) {
        // TODO: 商品メニュー表示
    }

    fun onBarcodeClick() {
        // TODO: バーコードスキャン
    }

    fun onMenuClick() {
        // TODO: メニュー表示
    }
}
