package com.allmoviedatabase.pandastore.model.favorites

import com.google.gson.annotations.SerializedName

data class FavoriteCheckResponse(
    @SerializedName("isFavorite")
    val isFavorite: Boolean
)