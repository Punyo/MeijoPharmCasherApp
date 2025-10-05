package com.punyo.casherapp.ui.register

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.punyo.casherapp.data.product.ProductRepository
import com.punyo.casherapp.data.product.model.ProductDataModel
import com.punyo.casherapp.data.transaction.TransactionRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock

class RegisterScreenViewModel(
    private val productRepository: ProductRepository,
    private val transactionRepository: TransactionRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(RegisterUiState())
    private val searchQueryFlow = MutableStateFlow("")

    val uiState: StateFlow<RegisterUiState> = _uiState.asStateFlow()
    val searchQuery: StateFlow<String> = searchQueryFlow.asStateFlow()

    @OptIn(ExperimentalCoroutinesApi::class)
    val pagingDataFlow: Flow<PagingData<ProductDataModel>> = searchQueryFlow
        .flatMapLatest { query ->
            if (query.isBlank()) {
                productRepository.getAllProductsPaged()
            } else {
                productRepository.searchProductsPaged(query)
            }
        }
        .cachedIn(viewModelScope)

    fun onBarcodeScanned(barcode: String) {
        viewModelScope.launch {
            try {
                productRepository.searchProducts(barcode).collect { products ->
                    val product = products.find { it.barcode == barcode }
                    product?.let { addProductToCart(it) }
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(error = e.message)
            }
        }
    }

    fun addProductToCart(product: ProductDataModel, quantity: Int = 1) {
        val currentCart = _uiState.value.cart
        val existingItemIndex = currentCart.items.indexOfFirst { it.product.id == product.id }

        val updatedItems = if (existingItemIndex != -1) {
            currentCart.items.toMutableList().apply {
                this[existingItemIndex] = this[existingItemIndex].copy(
                    quantity = this[existingItemIndex].quantity + quantity,
                )
            }
        } else {
            currentCart.items + CartItem(
                product = product,
                quantity = quantity,
                unitPrice = product.price,
            )
        }

        _uiState.value = _uiState.value.copy(
            cart = currentCart.copy(items = updatedItems),
        )
    }

    fun updateCartItemQuantity(index: Int, newQuantity: Int) {
        val currentCart = _uiState.value.cart
        if (index >= 0 && index < currentCart.items.size) {
            val updatedItems = currentCart.items.toMutableList().apply {
                if (newQuantity <= 0) {
                    removeAt(index)
                } else {
                    this[index] = this[index].copy(quantity = newQuantity)
                }
            }
            _uiState.value = _uiState.value.copy(
                cart = currentCart.copy(items = updatedItems),
            )
        }
    }

    fun removeCartItem(index: Int) {
        val currentCart = _uiState.value.cart
        if (index >= 0 && index < currentCart.items.size) {
            val updatedItems = currentCart.items.toMutableList().apply {
                removeAt(index)
            }
            _uiState.value = _uiState.value.copy(
                cart = currentCart.copy(items = updatedItems),
            )
        }
    }

    fun applyDiscount(discountPercent: Int) {
        val currentCart = _uiState.value.cart
        _uiState.value = _uiState.value.copy(
            cart = currentCart.copy(totalDiscountPercent = discountPercent),
        )
    }

    fun applyItemDiscount(index: Int, discountPercent: Int) {
        val currentCart = _uiState.value.cart
        if (index >= 0 && index < currentCart.items.size) {
            val updatedItems = currentCart.items.toMutableList().apply {
                this[index] = this[index].copy(discountPercent = discountPercent)
            }
            _uiState.value = _uiState.value.copy(
                cart = currentCart.copy(items = updatedItems),
            )
        }
    }

    fun updateSearchQuery(query: String) {
        searchQueryFlow.value = query
    }

    private fun clearCart() {
        _uiState.value = _uiState.value.copy(cart = Cart())
    }

    fun setInputMode(mode: InputMode) {
        _uiState.value = _uiState.value.copy(inputMode = mode)
    }

    fun confirmTransaction() {
        viewModelScope.launch {
            val cart = _uiState.value.cart
            val currentTime = Clock.System.now()

            val transactionItems = cart.toTransactionItems()
            transactionRepository.insertTransactionWithItems(
                createdAt = currentTime,
                items = transactionItems,
            )
            clearCart()
        }
    }
}
