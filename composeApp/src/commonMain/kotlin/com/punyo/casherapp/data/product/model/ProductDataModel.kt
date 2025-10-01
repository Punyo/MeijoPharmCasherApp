package com.punyo.casherapp.data.product.model

import org.joda.money.Money

data class ProductDataModel(
    val id: String,
    val name: String,
    val barcode: String? = null,
    val price: Money,
//    val category: String,
)
