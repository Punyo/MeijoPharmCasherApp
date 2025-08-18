package com.punyo.casherapp.data.product

import com.punyo.casherapp.data.product.model.ProductDataModel
import com.punyo.casherapp.data.product.source.ProductLocalDataSource
import kotlinx.coroutines.flow.Flow

class ProductRepositoryImpl(
    private val localDataSource: ProductLocalDataSource,
) : ProductRepository {
    override fun getAllProducts(): Flow<List<ProductDataModel>> = localDataSource.getAllProducts()

    override suspend fun getProductById(id: Long): ProductDataModel? = localDataSource.getProductById(id)

    override fun searchProducts(query: String): Flow<List<ProductDataModel>> = localDataSource.searchProducts(query)

    override suspend fun insertProduct(
        name: String,
        barcode: String?,
        price: Int,
        stock: Int,
    ) {
        localDataSource.insertProduct(
            name = name,
            barcode = barcode,
            price = price,
            stock = stock,
        )
    }

    override suspend fun updateProduct(product: ProductDataModel) {
        localDataSource.updateProduct(product)
    }

    override suspend fun deleteProduct(id: Long) {
        localDataSource.deleteProduct(id)
    }

    override suspend fun deleteAllProducts() {
        localDataSource.deleteAllProducts()
    }
}
