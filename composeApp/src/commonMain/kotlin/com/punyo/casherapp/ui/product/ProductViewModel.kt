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
    val products: List<ProductDataModel>? = null,
    val isLoading: Boolean = false,
) {
    val filteredProducts: List<ProductDataModel>? =
        if (searchText.isBlank()) {
            products
        } else {
            products?.filter { product ->
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

    fun deleteProduct(id: String) {
        viewModelScope.launch {
            repository.deleteProduct(id)
        }
        state.value.copy(
            products =
            state.value.products?.filter {
                it.id != id
            },
        )
    }

    fun addProduct(
        name: String,
        barcode: String?,
        price: UInt,
    ) {
        viewModelScope.launch {
            repository.insertProduct(
                name = name,
                barcode = barcode,
                price = price.toInt(),
            )
        }
    }

    fun updateProduct(
        id: String,
        name: String,
        barcode: String?,
        price: UInt,
    ) {
        viewModelScope.launch {
            val updatedProduct = ProductDataModel(
                id = id,
                name = name,
                barcode = barcode,
                price = price.toInt(),
            )
            repository.updateProduct(updatedProduct)
        }
    }
}
