package com.allmoviedatabase.pandastore.model.favorites

import com.google.gson.annotations.SerializedName

data class FavoriteRequest(
    @SerializedName("productId")
    val productId: Int
)