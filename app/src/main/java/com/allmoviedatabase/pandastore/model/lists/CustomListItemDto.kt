package com.allmoviedatabase.pandastore.model.lists

import android.os.Parcelable
import com.allmoviedatabase.pandastore.model.product.ProductDto
import kotlinx.parcelize.Parcelize

@Parcelize
data class CustomListItemDto(
    val id: Int, // Bu item'ın ID'si (Ürün ID değil!)
    val note: String?,
    val sortOrder: Int,
    val product: ProductDto
) : Parcelable