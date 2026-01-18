package com.allmoviedatabase.pandastore.model.cart

import com.google.gson.annotations.SerializedName

data class UpdateCartRequest(
    @SerializedName("quantity")
    val quantity: Int
)