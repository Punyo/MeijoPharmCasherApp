package com.punyo.casherapp.ui.register

import com.punyo.casherapp.data.product.model.ProductDataModel
import com.punyo.casherapp.data.transaction.model.TransactionItem
import com.punyo.casherapp.extensions.defaultCurrencyUnit
import com.punyo.casherapp.extensions.applyDiscount
import com.punyo.casherapp.extensions.discountAmount
import org.joda.money.Money

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
    val unitPrice: Money,
    val discountPercent: Float = 0f,
) {
    val originalPrice: Money = unitPrice.multipliedBy(quantity.toLong())
    val totalPrice: Money = originalPrice.applyDiscount(discountPercent)
    val discountAmount: Money = originalPrice.minus(totalPrice)
}

data class Cart(
    val items: List<CartItem> = emptyList(),
    val totalDiscountPercent: Float = 0f,
) {
    val originalSubtotal: Money = items.fold(Money.zero(defaultCurrencyUnit)) { acc, item -> acc.plus(item.originalPrice) }
    val subtotal: Money = items.fold(Money.zero(defaultCurrencyUnit)) { acc, item -> acc.plus(item.totalPrice) }
    val itemDiscountTotal: Money = items.fold(Money.zero(defaultCurrencyUnit)) { acc, item -> acc.plus(item.discountAmount) }
    val totalDiscount: Money = subtotal.applyDiscount(totalDiscountPercent).let { subtotal.minus(it) }
    val totalDiscountAmount: Money = itemDiscountTotal.plus(totalDiscount)
    val finalTotal: Money = subtotal.minus(totalDiscount)
    val totalQuantity: Int = items.sumOf { it.quantity }
}

fun Cart.toTransactionItems(): List<TransactionItem> = items.map { cartItem ->
    TransactionItem(
        quantity = cartItem.quantity,
        unitPrice = cartItem.unitPrice,
        productId = cartItem.product.id,
        discountAmount = cartItem.unitPrice.discountAmount(cartItem.discountPercent),
    )
}
