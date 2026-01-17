package com.allmoviedatabase.pandastore.model.product

import android.os.Parcelable
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
    val attributeValues: List<ProductAttributeValueDto>? = null,
    val stock: Int,
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