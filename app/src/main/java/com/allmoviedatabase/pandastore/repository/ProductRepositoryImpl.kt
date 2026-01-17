package com.allmoviedatabase.pandastore.repository

import com.allmoviedatabase.pandastore.data.remote.ApiService
import com.allmoviedatabase.pandastore.model.product.ProductFilterParams
import com.allmoviedatabase.pandastore.model.product.ProductListResponse
import io.reactivex.rxjava3.core.Single
import jakarta.inject.Inject

class ProductRepositoryImpl @Inject constructor(
    private val apiService: ApiService
) : ProductRepository {

    override fun getProducts(params: ProductFilterParams): Single<ProductListResponse> {
        return apiService.getProducts(
            page = params.page,
            search = params.searchQuery,
            categoryId = params.categoryId,
            minPrice = params.minPrice,
            maxPrice = params.maxPrice,
            sortBy = params.sortBy,
            brand = params.brand
        )
    }
}