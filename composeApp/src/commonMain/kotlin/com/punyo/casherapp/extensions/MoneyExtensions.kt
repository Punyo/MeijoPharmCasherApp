package com.punyo.casherapp.extensions

import org.joda.money.CurrencyUnit
import org.joda.money.Money
import java.math.RoundingMode

val defaultCurrencyUnit = CurrencyUnit.JPY

/**
 * Create Money from Int (assumes value is in yen)
 */
fun Int.toMoney(): Money = Money.of(defaultCurrencyUnit, this.toBigDecimal())

/**
 * Create Money from Long (assumes value is in yen)
 */
fun Long.toMoney(): Money = Money.of(defaultCurrencyUnit, this.toBigDecimal())

/**
 * Convert Money to Int (yen)
 */
fun Money.toInt(): Int = this.amount.setScale(0, RoundingMode.HALF_UP).toInt()

/**
 * Convert Money to Long (yen)
 */
fun Money.toLong(): Long = this.amount.setScale(0, RoundingMode.HALF_UP).toLong()

/**
 * Format Money for display (e.g., "¥1,234")
 */
fun Money.format(): String {
    val amount = this.amount.setScale(0, RoundingMode.HALF_UP).toString()
    val formatted = amount.reversed().chunked(3).joinToString(",").reversed()
    return "¥$formatted"
}

/**
 * Apply discount percentage to Money
 */
fun Money.applyDiscount(discountPercent: Float): Money {
    if (discountPercent <= 0f) return this
    val discountMultiplier = (100f - discountPercent) / 100f
    return this.multipliedBy(discountMultiplier.toBigDecimal(), RoundingMode.HALF_UP)
}

/**
 * Calculate discount amount
 */
fun Money.discountAmount(discountPercent: Float): Money {
    if (discountPercent <= 0f) return Money.zero(defaultCurrencyUnit)
    return this.minus(this.applyDiscount(discountPercent))
}
