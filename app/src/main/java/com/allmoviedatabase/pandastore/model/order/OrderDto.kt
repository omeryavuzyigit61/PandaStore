package com.allmoviedatabase.pandastore.model.order

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class OrderDto(
    val id: Int,
    val orderNumber: String, // ORD-2026-...
    val status: String,      // confirmed, shipped, delivered, cancelled
    val total: Double,
    val createdAt: String,   // Tarih
    val items: List<OrderItemDto> // İçindeki ürünler (Özet için ilkini gösterebiliriz)
) : Parcelable