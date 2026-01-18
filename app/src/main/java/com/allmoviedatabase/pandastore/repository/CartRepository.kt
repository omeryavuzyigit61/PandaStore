package com.allmoviedatabase.pandastore.repository

import com.allmoviedatabase.pandastore.model.cart.CartResponse
import com.allmoviedatabase.pandastore.model.order.CreateOrderRequest
import com.allmoviedatabase.pandastore.model.order.OrderResponse
import io.reactivex.rxjava3.core.Single

interface CartRepository {
    fun getCart(): Single<CartResponse>
    fun addToCart(productId: Int, quantity: Int): Single<CartResponse>
    fun updateQuantity(itemId: Int, quantity: Int): Single<Any>
    fun deleteItem(itemId: Int): Single<Any>
    fun clearCart(): Single<Any>
    fun createOrder(request: CreateOrderRequest): Single<OrderResponse>
}