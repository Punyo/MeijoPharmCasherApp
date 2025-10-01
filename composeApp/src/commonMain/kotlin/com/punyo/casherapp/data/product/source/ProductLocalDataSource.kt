package com.punyo.casherapp.data.product.source

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import com.punyo.casherapp.application.db.AppDatabase
import com.punyo.casherapp.data.product.model.ProductDataModel
import com.punyo.casherapp.extensions.toMoney
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import org.joda.money.Money
import java.util.UUID

class ProductLocalDataSource(
    private val database: AppDatabase,
) {
    fun getAllProducts(): Flow<List<ProductDataModel>> = database.productQueries
        .selectAll()
        .asFlow()
        .mapToList(Dispatchers.IO)
        .map { products ->
            products.map { product ->
                ProductDataModel(
                    id = product.id,
                    name = product.name,
                    barcode = product.barcode,
                    price = product.price.toMoney(),
                )
            }
        }

    suspend fun getProductById(id: String): ProductDataModel? = database.productQueries.selectById(id).executeAsOneOrNull()?.let { product ->
        ProductDataModel(
            id = product.id,
            name = product.name,
            barcode = product.barcode,
            price = product.price.toMoney(),
        )
    }

    fun searchProducts(query: String): Flow<List<ProductDataModel>> = database.productQueries
        .searchProducts(query, query)
        .asFlow()
        .mapToList(Dispatchers.IO)
        .map { products ->
            products.map { product ->
                ProductDataModel(
                    id = product.id,
                    name = product.name,
                    barcode = product.barcode,
                    price = product.price.toMoney(),
                )
            }
        }

    suspend fun insertProduct(
        name: String,
        barcode: String? = null,
        price: Money,
    ) {
        val uuid = UUID.randomUUID().toString()
        database.productQueries.insertProduct(
            id = uuid,
            name = name,
            barcode = barcode,
            price = price.amount.toLong(),
        )
    }

    suspend fun updateProduct(product: ProductDataModel) {
        database.productQueries.updateProduct(
            name = product.name,
            barcode = product.barcode,
            price = product.price.amount.toLong(),
            id = product.id,
        )
    }

    suspend fun deleteProduct(id: String) {
        database.productQueries.deleteProduct(id)
    }

    suspend fun deleteAllProducts() {
        database.productQueries.deleteAll()
    }
}
