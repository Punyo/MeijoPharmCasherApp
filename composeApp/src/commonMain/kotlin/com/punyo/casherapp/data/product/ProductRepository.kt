package com.punyo.casherapp.data.product

import com.punyo.casherapp.data.product.model.ProductDataModel
import kotlinx.coroutines.flow.Flow

interface ProductRepository {
    fun getAllProducts(): Flow<List<ProductDataModel>>

    suspend fun getProductById(id: Long): ProductDataModel?

    fun searchProducts(query: String): Flow<List<ProductDataModel>>

    suspend fun insertProduct(
        name: String,
        barcode: String?,
        price: Int,
        stock: Int,
    )

    suspend fun updateProduct(product: ProductDataModel)

    suspend fun deleteProduct(id: Long)

    suspend fun deleteAllProducts()
}
