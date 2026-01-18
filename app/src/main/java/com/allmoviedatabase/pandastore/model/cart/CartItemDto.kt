package com.allmoviedatabase.pandastore.model.cart

import com.allmoviedatabase.pandastore.model.product.ProductDto

data class CartItemDto(
    val id: Int, // Sepet öğesi ID'si (Ürün ID'si değil!)
    val quantity: Int,
    val priceAtAdd: Double,
    val product: ProductDto // Ürün detayları bunun içinde
)