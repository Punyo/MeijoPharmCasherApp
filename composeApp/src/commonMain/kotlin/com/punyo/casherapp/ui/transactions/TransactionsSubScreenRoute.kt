package com.punyo.casherapp.ui.transactions

import kotlinx.serialization.Serializable

@Serializable
data class TransactionsSubScreenRoute(
    val route: String,
    val period: String,
) {
    val timePeriod: TimePeriod
        get() = TimePeriod.valueOf(period)
}
