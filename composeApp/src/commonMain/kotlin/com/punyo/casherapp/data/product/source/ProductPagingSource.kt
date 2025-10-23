package com.punyo.casherapp.data.product.source

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.punyo.casherapp.application.db.AppDatabase
import com.punyo.casherapp.data.product.model.ProductDataModel
import com.punyo.casherapp.extensions.toMoney
import java.io.IOException

class ProductPagingSource(
    private val database: AppDatabase,
    private val query: String? = null,
) : PagingSource<Int, ProductDataModel>() {

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, ProductDataModel> = try {
        val page = params.key ?: 0
        val offset = page * params.loadSize

        val products = if (query.isNullOrEmpty()) {
            database.productQueries.selectWithPaging(
                params.loadSize.toLong(),
                offset.toLong(),
            ).executeAsList()
        } else {
            database.productQueries.searchWithPaging(
                query,
                query,
                params.loadSize.toLong(),
                offset.toLong(),
            ).executeAsList()
        }

        val productDataModels = products.map { product ->
            ProductDataModel(
                id = product.id,
                name = product.name,
                barcode = product.barcode,
                price = product.price.toMoney(),
            )
        }

        LoadResult.Page(
            data = productDataModels,
            prevKey = if (page == 0) null else page - 1,
            nextKey = if (products.size < params.loadSize) null else page + 1,
        )
    } catch (exception: IOException) {
        LoadResult.Error(exception)
    }

    override fun getRefreshKey(state: PagingState<Int, ProductDataModel>): Int? = state.anchorPosition?.let { anchorPosition ->
        state.closestPageToPosition(anchorPosition)?.prevKey?.plus(1)
            ?: state.closestPageToPosition(anchorPosition)?.nextKey?.minus(1)
    }
}
