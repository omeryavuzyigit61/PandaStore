package com.allmoviedatabase.pandastore.repository

import com.allmoviedatabase.pandastore.model.product.ProductDto
import com.allmoviedatabase.pandastore.model.product.ProductFilterParams
import com.allmoviedatabase.pandastore.model.product.ProductListResponse
import com.allmoviedatabase.pandastore.model.review.CreateReviewRequest
import com.allmoviedatabase.pandastore.model.review.ReviewDto
import io.reactivex.rxjava3.core.Single

interface ProductRepository {
    fun getProducts(params: ProductFilterParams): Single<ProductListResponse>
    fun getProductDetail(id: Int): Single<ProductDto>

    fun getProductReviews(productId: Int): Single<List<ReviewDto>>
    fun addReview(request: CreateReviewRequest): Single<ReviewDto>

    fun updateReview(reviewId: Int, request: CreateReviewRequest): Single<ReviewDto>
    fun deleteReview(reviewId: Int): Single<Unit>
}