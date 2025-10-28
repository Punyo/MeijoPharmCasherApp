package com.punyo.casherapp.data.product

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.punyo.casherapp.application.db.AppDatabase
import com.punyo.casherapp.data.product.model.ProductDataModel
import com.punyo.casherapp.data.product.source.ProductLocalDataSource
import com.punyo.casherapp.data.product.source.ProductPagingSource
import com.punyo.casherapp.ui.register.ProductSortOption
import kotlinx.coroutines.flow.Flow
import org.joda.money.Money

class ProductRepositoryImpl(
    private val localDataSource: ProductLocalDataSource,
    private val database: AppDatabase,
) : ProductRepository {
    override fun getAllProducts(): Flow<List<ProductDataModel>> = localDataSource.getAllProducts()

    override suspend fun getProductById(id: String): ProductDataModel? = localDataSource.getProductById(id)

    override fun searchProducts(query: String): Flow<List<ProductDataModel>> = localDataSource.searchProducts(query)

    override suspend fun insertProduct(
        name: String,
        barcode: String?,
        price: Money,
    ) {
        localDataSource.insertProduct(
            name = name,
            barcode = barcode,
            price = price,
        )
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

    override fun getAllProductsPaged(sortOption: ProductSortOption): Flow<PagingData<ProductDataModel>> = Pager(
        config = PagingConfig(
            pageSize = 10,
            enablePlaceholders = false,
        ),
        pagingSourceFactory = { ProductPagingSource(database, sortOption = sortOption) },
    ).flow

    override fun searchProductsPaged(query: String, sortOption: ProductSortOption): Flow<PagingData<ProductDataModel>> = Pager(
        config = PagingConfig(
            pageSize = 10,
            enablePlaceholders = false,
        ),
        pagingSourceFactory = { ProductPagingSource(database, query, sortOption) },
    ).flow
}
