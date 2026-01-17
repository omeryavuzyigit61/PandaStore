package com.allmoviedatabase.pandastore.model.review

import com.google.gson.annotations.SerializedName

data class CreateReviewRequest(
    @SerializedName("productId") val productId: Int,
    @SerializedName("rating") val rating: Int,
    @SerializedName("title") val title: String,
    @SerializedName("comment") val comment: String
)