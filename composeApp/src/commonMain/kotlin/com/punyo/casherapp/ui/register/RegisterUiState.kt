package com.punyo.casherapp.ui.register

import com.punyo.casherapp.data.product.model.ProductDataModel
import com.punyo.casherapp.data.transaction.model.TransactionItem
import kotlin.math.roundToInt

data class RegisterUiState(
    val cart: Cart = Cart(),
    val inputMode: InputMode = InputMode.SEARCH,
    val searchQuery: String = "",
    val error: String? = null,
)

enum class InputMode {
    CAMERA,
    SEARCH,
}

data class CartItem(
    val product: ProductDataModel,
    val quantity: Int = 1,
    val unitPrice: Int,
    val discountPercent: Float = 0f,
) {
    val originalPrice: Int = unitPrice * quantity
    val totalPrice: Int = (unitPrice * quantity * (1 - discountPercent / 100)).roundToInt()
    val discountAmount: Int = originalPrice - totalPrice
}

data class Cart(
    val items: List<CartItem> = emptyList(),
    val totalDiscountPercent: Float = 0f,
) {
    val originalSubtotal: Int = items.sumOf { it.originalPrice }
    val subtotal: Int = items.sumOf { it.totalPrice }
    val itemDiscountTotal: Int = items.sumOf { it.discountAmount }
    val totalDiscount: Int = (subtotal * totalDiscountPercent / 100).roundToInt()
    val totalDiscountAmount: Int = itemDiscountTotal + totalDiscount
    val finalTotal: Int = subtotal - totalDiscount
    val totalQuantity: Int = items.sumOf { it.quantity }
}

fun Cart.toTransactionItems(): List<TransactionItem> = items.map { cartItem ->
    TransactionItem(
        quantity = cartItem.quantity,
        unitPrice = cartItem.unitPrice,
        productId = cartItem.product.id,
        discountAmount = (cartItem.unitPrice * cartItem.discountPercent / 100).roundToInt(),
    )
}
