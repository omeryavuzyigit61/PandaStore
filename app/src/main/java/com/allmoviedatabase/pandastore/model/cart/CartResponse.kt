package com.allmoviedatabase.pandastore.model.cart

data class CartResponse(
    val cart: CartDto,
    val itemCount: Int,
    val subtotal: Double,
    val total: Double
)