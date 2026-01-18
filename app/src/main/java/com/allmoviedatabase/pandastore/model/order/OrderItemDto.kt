package com.allmoviedatabase.pandastore.model.order

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class OrderItemDto(
    val id: Int,
    val quantity: Int,
    val unitPrice: Double,
    val productName: String,
    val productImage: String?
) : Parcelable