package com.allmoviedatabase.pandastore.model.order

data class CreateOrderRequest(
    val addressId: Int? = null,
    val shippingName: String? = null,
    val shippingPhone: String? = null,
    val shippingCity: String? = null,
    val shippingDistrict: String? = null,
    val shippingAddress: String? = null,
    val shippingZipCode: String? = null,
    val customerNote: String? = null
)