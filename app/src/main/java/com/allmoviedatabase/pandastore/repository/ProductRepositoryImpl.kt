package com.allmoviedatabase.pandastore.repository

import com.allmoviedatabase.pandastore.data.remote.ApiService
import com.allmoviedatabase.pandastore.model.product.ProductDto
import com.allmoviedatabase.pandastore.model.product.ProductFilterParams
import com.allmoviedatabase.pandastore.model.product.ProductListResponse
import com.allmoviedatabase.pandastore.model.review.CreateReviewRequest
import com.allmoviedatabase.pandastore.model.review.ReviewDto
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

    override fun getProductDetail(id: Int): Single<ProductDto> {
        return apiService.getProductDetail(id)
    }
    override fun getProductReviews(productId: Int): Single<List<ReviewDto>> {
        return apiService.getProductReviews(productId)
    }

    override fun addReview(request: CreateReviewRequest): Single<ReviewDto> {
        return apiService.addReview(request)
    }

    override fun updateReview(reviewId: Int, request: CreateReviewRequest): Single<ReviewDto> {
        return apiService.updateReview(reviewId, request)
    }

    override fun deleteReview(reviewId: Int): Single<Unit> {
        return apiService.deleteReview(reviewId)
    }
}