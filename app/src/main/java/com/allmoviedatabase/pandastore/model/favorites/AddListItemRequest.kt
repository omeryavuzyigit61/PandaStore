package com.allmoviedatabase.pandastore.model.favorites

import com.google.gson.annotations.SerializedName

data class AddListItemRequest(
    @SerializedName("productId")
    val productId: Int
)