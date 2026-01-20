package com.allmoviedatabase.pandastore.repository

import com.allmoviedatabase.pandastore.data.remote.ApiService
import com.allmoviedatabase.pandastore.model.favorites.FavoriteRequest
import com.allmoviedatabase.pandastore.model.lists.AddListItemRequest
import com.allmoviedatabase.pandastore.model.lists.CustomListDto
import com.allmoviedatabase.pandastore.model.product.ProductDto
import com.allmoviedatabase.pandastore.model.product.ProductFilterParams
import com.allmoviedatabase.pandastore.model.product.ProductListResponse
import com.allmoviedatabase.pandastore.model.review.CreateReviewRequest
import com.allmoviedatabase.pandastore.model.review.ReviewDto
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.schedulers.Schedulers
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

// --- FAVORİLER İMPLEMENTASYONU ---

    override fun getFavorites(): Single<List<ProductDto>> {
        return apiService.getFavorites()
            .map { favoriteList ->
                // API bize favori listesi dönüyor, biz içindeki 'product'ları alıp yeni liste yapıyoruz
                favoriteList.map { it.product }
            }
            .subscribeOn(Schedulers.io())
    }

    override fun checkFavoriteStatus(productId: Int): Single<Boolean> {
        return apiService.checkIsFavorite(productId)
            .map { it.isFavorite }
            .subscribeOn(Schedulers.io())
    }

    override fun addToFavorites(productId: Int): Single<Unit> {
        return apiService.addToFavorites(FavoriteRequest(productId))
            .map { Unit }
            .subscribeOn(Schedulers.io())
    }

    override fun removeFromFavorites(productId: Int): Single<Unit> {
        return apiService.removeFromFavorites(productId)
            .map { Unit }
            .subscribeOn(Schedulers.io())
    }

    // --- LİSTE İŞLEMLERİ ---
    override fun getUserLists(): Single<List<CustomListDto>> {
        return apiService.getMyLists().subscribeOn(Schedulers.io())
    }

    override fun addProductToList(listId: Int, productId: Int): Single<Unit> {
        return apiService.addProductToList(listId, AddListItemRequest(productId))
            .map { Unit }
            .subscribeOn(Schedulers.io())
    }
}
