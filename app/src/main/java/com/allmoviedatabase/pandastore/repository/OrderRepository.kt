package com.allmoviedatabase.pandastore.repository

import com.allmoviedatabase.pandastore.model.order.OrderDto
import com.allmoviedatabase.pandastore.model.order.OrderResponse
import io.reactivex.rxjava3.core.Single

interface OrderRepository {
    fun getOrders(): Single<List<OrderDto>>
    fun getOrderDetail(id: Int): Single<OrderResponse>
    fun cancelOrder(id: Int): Single<Any>
}