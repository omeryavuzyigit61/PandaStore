package com.allmoviedatabase.pandastore.model.product

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class ProductAttributeValueDto(
    @SerializedName("attributeId") val attributeId: Int,
    @SerializedName("attributeName") val attributeName: String, // "RAM"
    @SerializedName("attributeSlug") val attributeSlug: String,
    @SerializedName("value") val value: String                  // "8GB"
) : Parcelable