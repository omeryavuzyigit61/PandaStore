package com.allmoviedatabase.pandastore.data.remote

import com.allmoviedatabase.pandastore.model.address.AddressDto
import com.allmoviedatabase.pandastore.model.address.AddressRequest
import com.allmoviedatabase.pandastore.model.login.LoginRequest
import com.allmoviedatabase.pandastore.model.login.LoginResponse
import com.allmoviedatabase.pandastore.model.product.ProductDto
import com.allmoviedatabase.pandastore.model.product.ProductListResponse
import com.allmoviedatabase.pandastore.model.register.RegisterRequest
import com.allmoviedatabase.pandastore.model.register.RegisterResponse
import com.allmoviedatabase.pandastore.model.register.UserDto
import com.allmoviedatabase.pandastore.model.review.CreateReviewRequest
import com.allmoviedatabase.pandastore.model.review.ReviewDto
import io.reactivex.rxjava3.core.Single
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiService {

    @POST("auth/login")
    fun login(@Body request: LoginRequest): Single<LoginResponse>

    @POST("auth/refresh")
    fun refreshToken(): Single<LoginResponse>

    @POST("users")
    fun register(@Body request: RegisterRequest): Single<RegisterResponse>

    @GET("users/profile")
    fun getProfile(): Single<UserDto>

    @GET("addresses")
    fun getAddresses(): Single<List<AddressDto>>

    @POST("addresses")
    fun addAddress(@Body request: AddressRequest): Single<AddressDto>

    @PATCH("addresses/{id}")
    fun updateAddress(@Path("id") id: Int, @Body request: AddressRequest): Single<AddressDto>

    @DELETE("addresses/{id}")
    fun deleteAddress(@Path("id") id: Int): Single<Unit> // Unit döner (boş)

    @GET("products")
    fun getProducts(
        @Query("page") page: Int = 1,
        @Query("limit") limit: Int = 20,
        @Query("search") search: String? = null,
        @Query("categoryId") categoryId: Int? = null,
        @Query("minPrice") minPrice: Double? = null,
        @Query("maxPrice") maxPrice: Double? = null,
        @Query("sortBy") sortBy: String? = null, // price_low, price_high, newest, popular
        @Query("brand") brand: String? = null
    ): Single<ProductListResponse>

    @GET("products/{id}")
    fun getProductDetail(@Path("id") id: Int): Single<ProductDto>

    @GET("reviews/product/{id}")
    fun getProductReviews(@Path("id") productId: Int): Single<List<ReviewDto>>

    // 4️⃣ Yorum Yap (Token gerektirir - Retrofit Interceptor halleder)
    @POST("reviews")
    fun addReview(@Body request: CreateReviewRequest): Single<ReviewDto>

    @PATCH("reviews/{id}")
    fun updateReview(@Path("id") reviewId: Int, @Body request: CreateReviewRequest): Single<ReviewDto>

    // 6️⃣ Yorum Sil
    @DELETE("reviews/{id}")
    fun deleteReview(@Path("id") reviewId: Int): Single<Unit>
}