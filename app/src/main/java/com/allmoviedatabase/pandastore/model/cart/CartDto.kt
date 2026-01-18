package com.allmoviedatabase.pandastore.model.cart

data class CartDto(
    val id: Int,
    val items: List<CartItemDto>
)