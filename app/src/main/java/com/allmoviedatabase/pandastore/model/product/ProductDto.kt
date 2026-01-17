package com.allmoviedatabase.pandastore.model.product

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize
import kotlin.collections.find
import kotlin.collections.firstOrNull

@Parcelize
data class ProductDto(
    val id: Int,
    val name: String,
    val slug: String,
    val price: Double,
    val compareAtPrice: Double?, // İndirimsiz fiyat (opsiyonel)
    val discountRate: Int?,
    @SerializedName("attributeValues")
    val attributeValues: List<ProductAttributeValueDto>?,
    val stock: Int,
    @SerializedName("description")
    val description: String?,
    val brand: String?,
    val averageRating: Double,
    val reviewCount: Int,
    val images: List<ProductImageDto>?,
    val isFeatured: Boolean
) : Parcelable {
    // Vitrin görselini almak için yardımcı fonksiyon
    fun getThumbnailUrl(): String? {
        return images?.find { it.isPrimary }?.url ?: images?.firstOrNull()?.url
    }
}