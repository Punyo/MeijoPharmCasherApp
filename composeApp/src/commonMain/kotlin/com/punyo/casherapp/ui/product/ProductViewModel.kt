package com.punyo.casherapp.ui.product

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.punyo.casherapp.data.product.ProductRepository
import com.punyo.casherapp.data.product.model.ProductDataModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class ProductUiState(
    val searchText: String = "",
    val products: List<ProductDataModel> = emptyList(),
    val showAddProductDialog: Boolean = false,
    val editingProduct: ProductDataModel? = null,
    val isLoading: Boolean = false,
) {
    val filteredProducts: List<ProductDataModel> =
        if (searchText.isBlank()) {
            products
        } else {
            products.filter { product ->
                product.name.contains(searchText, ignoreCase = true)
            }
        }
}

class ProductViewModel(
    private val repository: ProductRepository,
) : ViewModel() {
    private val state = MutableStateFlow(ProductUiState())
    val uiState: StateFlow<ProductUiState> = state.asStateFlow()

    init {
        loadProducts()
    }

    private fun loadProducts() {
        viewModelScope.launch {
            repository.getAllProducts().collect { products ->
                state.value = state.value.copy(products = products)
            }
        }
    }

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

    fun showEditProductDialog(product: ProductDataModel) {
        state.value = state.value.copy(editingProduct = product)
    }

    fun hideEditProductDialog() {
        state.value = state.value.copy(editingProduct = null)
    }

    fun deleteProduct(id: Long) {
        viewModelScope.launch {
            repository.deleteProduct(id)
        }
        state.value.copy(
            products =
                state.value.products.filter {
                    it.id != id
                },
        )
    }

    fun addProduct(
        name: String,
        barcode: String,
        price: Int,
        stock: Int,
    ) {
        viewModelScope.launch {
            repository.insertProduct(
                name = name,
                barcode = barcode.takeIf { it.isNotBlank() },
                price = price,
                stock = stock,
            )
            hideAddProductDialog()
        }
    }

    fun updateProduct(
        id: Long,
        name: String,
        barcode: String,
        price: Int,
        stock: Int,
    ) {
        viewModelScope.launch {
            val updatedProduct = ProductDataModel(
                id = id,
                name = name,
                barcode = barcode.takeIf { it.isNotBlank() },
                price = price,
                stock = stock,
            )
            repository.updateProduct(updatedProduct)
            hideEditProductDialog()
        }
    }

    fun onProductClick(product: ProductDataModel) {
        // TODO: 商品詳細画面への遷移
    }

    fun onProductMenuClick(product: ProductDataModel) {
        // TODO: 商品メニュー表示
    }

    fun onBarcodeClick() {
        // TODO: バーコードスキャン
    }

    fun onMenuClick() {
        // TODO: メニュー表示
    }
}
