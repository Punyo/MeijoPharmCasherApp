package com.punyo.casherapp.data.product.model

data class ProductDataModel(
    val id: String,
    val name: String,
    val barcode: String,
    val price: Int,
//    val category: String,
    val stock: Int,
)
