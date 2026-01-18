package com.allmoviedatabase.pandastore.repository

import com.allmoviedatabase.pandastore.data.remote.ApiService
import com.allmoviedatabase.pandastore.model.order.OrderDto
import com.allmoviedatabase.pandastore.model.order.OrderResponse
import io.reactivex.rxjava3.core.Single
import javax.inject.Inject

class OrderRepositoryImpl @Inject constructor(
    private val api: ApiService
) : OrderRepository {

    override fun getOrders(): Single<List<OrderDto>> {
        return api.getOrders()
    }

    override fun getOrderDetail(id: Int): Single<OrderResponse> {
        return api.getOrderDetail(id)
    }

    override fun cancelOrder(id: Int): Single<Any> {
        return api.cancelOrder(id)
    }
}