package com.allmoviedatabase.pandastore.repository

import com.allmoviedatabase.pandastore.data.remote.ApiService
import com.allmoviedatabase.pandastore.model.cart.AddToCartRequest
import com.allmoviedatabase.pandastore.model.cart.CartResponse
import com.allmoviedatabase.pandastore.model.cart.UpdateCartRequest
import com.allmoviedatabase.pandastore.model.order.CreateOrderRequest
import com.allmoviedatabase.pandastore.model.order.OrderResponse
import io.reactivex.rxjava3.core.Single
import javax.inject.Inject

class CartRepositoryImpl @Inject constructor(
    private val api: ApiService
) : CartRepository {

    override fun getCart(): Single<CartResponse> {
        return api.getCart()
    }

    override fun addToCart(productId: Int, quantity: Int): Single<CartResponse> {
        return api.addToCart(AddToCartRequest(productId, quantity))
    }

    override fun updateQuantity(itemId: Int, quantity: Int): Single<Any> {
        return api.updateCartItem(itemId, UpdateCartRequest(quantity))
    }

    override fun deleteItem(itemId: Int): Single<Any> {
        return api.deleteCartItem(itemId)
    }

    override fun clearCart(): Single<Any> {
        return api.clearCart()
    }

    override fun createOrder(request: CreateOrderRequest): Single<OrderResponse> {
        return api.createOrder(request)
    }
}