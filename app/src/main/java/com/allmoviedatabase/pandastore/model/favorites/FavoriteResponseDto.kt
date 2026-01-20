package com.allmoviedatabase.pandastore.model.favorites

import com.allmoviedatabase.pandastore.model.product.ProductDto
import com.google.gson.annotations.SerializedName

data class FavoriteResponseDto(
    @SerializedName("id")
    val id: Int,
    @SerializedName("productId")
    val productId: Int,
    @SerializedName("product")
    val product: ProductDto // Bizim asıl ihtiyacımız olan bu
)