package com.allmoviedatabase.pandastore.data.remote

import com.allmoviedatabase.pandastore.model.address.AddressDto
import com.allmoviedatabase.pandastore.model.address.AddressRequest
import com.allmoviedatabase.pandastore.model.cart.AddToCartRequest
import com.allmoviedatabase.pandastore.model.cart.CartResponse
import com.allmoviedatabase.pandastore.model.cart.UpdateCartRequest
import com.allmoviedatabase.pandastore.model.favorites.FavoriteCheckResponse
import com.allmoviedatabase.pandastore.model.favorites.FavoriteRequest
import com.allmoviedatabase.pandastore.model.favorites.FavoriteResponseDto
import com.allmoviedatabase.pandastore.model.lists.AddListItemRequest
import com.allmoviedatabase.pandastore.model.lists.CreateListRequest
import com.allmoviedatabase.pandastore.model.lists.CustomListDto
import com.allmoviedatabase.pandastore.model.lists.DiscoverListResponse
import com.allmoviedatabase.pandastore.model.lists.UpdateListRequest
import com.allmoviedatabase.pandastore.model.login.LoginRequest
import com.allmoviedatabase.pandastore.model.login.LoginResponse
import com.allmoviedatabase.pandastore.model.order.CreateOrderRequest
import com.allmoviedatabase.pandastore.model.order.OrderDto
import com.allmoviedatabase.pandastore.model.order.OrderResponse
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
    fun updateReview(
        @Path("id") reviewId: Int,
        @Body request: CreateReviewRequest
    ): Single<ReviewDto>

    // 6️⃣ Yorum Sil
    @DELETE("reviews/{id}")
    fun deleteReview(@Path("id") reviewId: Int): Single<Unit>

    @GET("cart")
    fun getCart(): Single<CartResponse>

    @POST("cart")
    fun addToCart(@Body request: AddToCartRequest): Single<CartResponse>

    @PATCH("cart/items/{id}")
    fun updateCartItem(
        @Path("id") itemId: Int,
        @Body request: UpdateCartRequest
    ): Single<Any> // Cevap body önemli değilse Any

    @DELETE("cart/items/{id}")
    fun deleteCartItem(@Path("id") itemId: Int): Single<Any>

    @DELETE("cart")
    fun clearCart(): Single<Any>

    // --- ORDER (SİPARİŞ) ---
    @POST("orders")
    fun createOrder(@Body request: CreateOrderRequest): Single<OrderResponse>

    @GET("orders")
    fun getOrders(): Single<List<OrderDto>>

    // Tekil Sipariş Detayı
    @GET("orders/{id}")
    fun getOrderDetail(@Path("id") id: Int): Single<OrderResponse> // Detaylı response kullanıyoruz

    // İptal Et
    @PATCH("orders/{id}/cancel")
    fun cancelOrder(@Path("id") id: Int): Single<Any>

    // --- FAVORİ İŞLEMLERİ ---

    // 1. Favorileri Listele (BU EKSİKTİ, EKLENDİ)
    @GET("favorites")
    fun getFavorites(): Single<List<FavoriteResponseDto>>

    // 2. Ürün Favorilerde mi Kontrol Et
    @GET("favorites/check/{productId}")
    fun checkIsFavorite(@Path("productId") id: Int): Single<FavoriteCheckResponse>

    // 3. Favorilere Ekle
    @POST("favorites")
    fun addToFavorites(@Body request: FavoriteRequest): Single<Unit>

    // 4. Favorilerden Çıkar
    @DELETE("favorites/{productId}")
    fun removeFromFavorites(@Path("productId") id: Int): Single<Unit>


    // --- Liste İşlewmleri ---

    @GET("lists")
    fun getMyLists(): Single<List<CustomListDto>>

    // 2. Liste Oluştur
    @POST("lists")
    fun createList(@Body request: CreateListRequest): Single<CustomListDto>

    // 3. Tek Liste Detayı (Ürünlerle birlikte)
    @GET("lists/{id}")
    fun getListDetail(@Path("id") id: Int): Single<CustomListDto>

    // 4. Liste Sil
    @DELETE("lists/{id}")
    fun deleteList(@Path("id") id: Int): Single<Any>

    @PATCH("lists/{id}")
    fun updateList(@Path("id") id: Int, @Body request: UpdateListRequest): Single<CustomListDto>

    // 6. Keşfet / Public Listeler (EKSİK OLAN BU)
    @GET("lists/discover")
    fun getDiscoverLists(
        @Query("page") page: Int = 1,
        @Query("limit") limit: Int = 20
    ): Single<DiscoverListResponse>

    // 5. Listeye Ürün Ekle
    @POST("lists/{id}/items")
    fun addProductToList(@Path("id") listId: Int, @Body request: AddListItemRequest): Single<Any>

    // 6. Listeden Ürün Çıkar
    @DELETE("lists/{listId}/items/{productId}")
    fun removeProductFromList(
        @Path("listId") listId: Int,
        @Path("productId") productId: Int // Dikkat: API productId istiyor, item ID değil.
    ): Single<Any>
}