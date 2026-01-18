package com.allmoviedatabase.pandastore.model.order

data class OrderResponse(
    val id: Int,
    val orderNumber: String,
    val status: String,
    val total: Double,
    val createdAt: String
)