package com.allmoviedatabase.pandastore.model.lists

import com.allmoviedatabase.pandastore.model.product.ProductDto
import com.google.gson.annotations.SerializedName

data class ListItemDto(
    @SerializedName("id")
    val id: Int,

    @SerializedName("listId")
    val listId: Int,

    @SerializedName("productId")
    val productId: Int,

    @SerializedName("note")
    val note: String?,

    // JSON'daki "product": { ... } objesini buraya bağlıyoruz
    @SerializedName("product")
    val product: ProductDto
)