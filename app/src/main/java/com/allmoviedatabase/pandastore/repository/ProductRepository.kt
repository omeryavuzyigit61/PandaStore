package com.allmoviedatabase.pandastore.repository

import com.allmoviedatabase.pandastore.model.product.ProductFilterParams
import com.allmoviedatabase.pandastore.model.product.ProductListResponse
import io.reactivex.rxjava3.core.Single

interface ProductRepository {
    fun getProducts(params: ProductFilterParams): Single<ProductListResponse>
}