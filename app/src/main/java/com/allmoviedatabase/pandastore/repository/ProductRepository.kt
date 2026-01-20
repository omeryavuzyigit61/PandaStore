package com.allmoviedatabase.pandastore.repository

import com.allmoviedatabase.pandastore.model.lists.CustomListDto
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

    // --- FAVORİLER ---

    fun getFavorites(): Single<List<ProductDto>>

    fun checkFavoriteStatus(productId: Int): Single<Boolean>
    fun addToFavorites(productId: Int): Single<Unit>
    fun removeFromFavorites(productId: Int): Single<Unit>

    // --- LİSTELER ---
    fun getUserLists(): Single<List<CustomListDto>>
    fun addProductToList(listId: Int, productId: Int): Single<Unit>
}