package com.punyo.casherapp.extensions

import org.joda.money.CurrencyUnit
import org.joda.money.Money
import org.joda.money.format.MoneyFormatter
import org.joda.money.format.MoneyFormatterBuilder
import java.math.BigDecimal
import java.math.RoundingMode

val defaultCurrencyUnit = CurrencyUnit.JPY!!
private val defaultScale = 5
private val defaultRoundingMode = RoundingMode.HALF_EVEN

private val defaultMoneyFormatter: MoneyFormatter = MoneyFormatterBuilder()
    .appendLiteral("¥")
    .appendAmountLocalized()
    .toFormatter()

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
fun Money.toInt(): Int = this.amount.setScale(defaultScale, defaultRoundingMode).toInt()

/**
 * Convert Money to Long (yen)
 */
fun Money.toLong(): Long = this.amount.setScale(defaultScale, defaultRoundingMode).toLong()

/**
 * Convert Money to Double (yen)
 */
fun Money.toDouble(): Double = this.amount.setScale(defaultScale, defaultRoundingMode).toDouble()

/**
 * Format Money for display (e.g., "¥1,234")
 */
fun Money.format(): String = defaultMoneyFormatter.print(this)

/**
 * Apply discount to Money value
 * @param discountPercent Discount percentage (0-100)
 * @return Money value after applying discount
 */
fun Money.applyDiscount(discountPercent: Int): Money {
    require(discountPercent in 0..100) {
        "Discount percent must be between 0 and 100, but was $discountPercent"
    }
    val discountMultiplier = BigDecimal(100 - discountPercent)
        .divide(BigDecimal(100), defaultScale, defaultRoundingMode)
    return this.multipliedBy(discountMultiplier, defaultRoundingMode)
}
