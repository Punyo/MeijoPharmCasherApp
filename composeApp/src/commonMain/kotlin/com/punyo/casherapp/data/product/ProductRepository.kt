package com.punyo.casherapp.data.product

import com.punyo.casherapp.data.product.model.ProductDataModel
import kotlinx.coroutines.flow.Flow

interface ProductRepository {
    fun getAllProducts(): Flow<List<ProductDataModel>>

    suspend fun getProductById(id: String): ProductDataModel?

    fun searchProducts(query: String): Flow<List<ProductDataModel>>

    suspend fun insertProduct(
        name: String,
        barcode: String?,
        price: Int,
        soldUnit: Int,
        salesAmount: Int,
    )

    suspend fun updateProduct(product: ProductDataModel)

    suspend fun deleteProduct(id: String)

    suspend fun deleteAllProducts()
}
