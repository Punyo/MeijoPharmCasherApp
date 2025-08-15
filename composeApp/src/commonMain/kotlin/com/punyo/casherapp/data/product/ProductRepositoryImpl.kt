package com.punyo.casherapp.data.product

import com.punyo.casherapp.data.product.model.ProductDataModel
import com.punyo.casherapp.data.product.source.ProductLocalDataSource
import kotlinx.coroutines.flow.Flow

class ProductRepositoryImpl(
    private val localDataSource: ProductLocalDataSource,
) : ProductRepository {
    override fun getAllProducts(): Flow<List<ProductDataModel>> = localDataSource.getAllProducts()

    override suspend fun getProductById(id: String): ProductDataModel? = localDataSource.getProductById(id)

    override fun searchProducts(query: String): Flow<List<ProductDataModel>> = localDataSource.searchProducts(query)

    override suspend fun insertProduct(product: ProductDataModel) {
        localDataSource.insertProduct(product)
    }

    override suspend fun updateProduct(product: ProductDataModel) {
        localDataSource.updateProduct(product)
    }

    override suspend fun deleteProduct(id: String) {
        localDataSource.deleteProduct(id)
    }

    override suspend fun deleteAllProducts() {
        localDataSource.deleteAllProducts()
    }
}
