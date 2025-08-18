package com.punyo.casherapp.data.product.source

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import com.punyo.casherapp.application.db.AppDatabase
import com.punyo.casherapp.data.product.model.ProductDataModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class ProductLocalDataSource(
    private val database: AppDatabase,
) {
    fun getAllProducts(): Flow<List<ProductDataModel>> =
        database.productQueries
            .selectAll()
            .asFlow()
            .mapToList(Dispatchers.IO)
            .map { products ->
                products.map { product ->
                    ProductDataModel(
                        id = product.id,
                        name = product.name,
                        barcode = product.barcode,
                        price = product.price.toInt(),
                        stock = product.stock.toInt(),
                    )
                }
            }

    suspend fun getProductById(id: Long): ProductDataModel? =
        database.productQueries.selectById(id).executeAsOneOrNull()?.let { product ->
            ProductDataModel(
                id = product.id,
                name = product.name,
                barcode = product.barcode,
                price = product.price.toInt(),
                stock = product.stock.toInt(),
            )
        }

    fun searchProducts(query: String): Flow<List<ProductDataModel>> =
        database.productQueries
            .searchProducts(query, query)
            .asFlow()
            .mapToList(Dispatchers.IO)
            .map { products ->
                products.map { product ->
                    ProductDataModel(
                        id = product.id,
                        name = product.name,
                        barcode = product.barcode,
                        price = product.price.toInt(),
                        stock = product.stock.toInt(),
                    )
                }
            }

    suspend fun insertProduct(
        name: String,
        barcode: String? = null,
        price: Int,
        stock: Int,
    ) {
        database.productQueries.insertProduct(
            name = name,
            barcode = barcode,
            price = price.toLong(),
            stock = stock.toLong(),
        )
    }

    suspend fun updateProduct(product: ProductDataModel) {
        database.productQueries.updateProduct(
            name = product.name,
            barcode = product.barcode,
            price = product.price.toLong(),
            stock = product.stock.toLong(),
            id = product.id,
        )
    }

    suspend fun deleteProduct(id: Long) {
        database.productQueries.deleteProduct(id)
    }

    suspend fun deleteAllProducts() {
        database.productQueries.deleteAll()
    }
}
