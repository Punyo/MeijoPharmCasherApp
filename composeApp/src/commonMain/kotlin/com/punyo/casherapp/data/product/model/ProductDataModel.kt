package com.punyo.casherapp.data.product.model

data class ProductDataModel(
    val id: String,
    val name: String,
    val barcode: String? = null,
    val price: Int,
//    val category: String,
)
