package com.allmoviedatabase.pandastore.model.order

import com.google.gson.annotations.SerializedName

data class CreateOrderRequest(
    // Adres ID'si varsa gönderilir (Şimdilik null göndereceğiz)
    @SerializedName("addressId")
    val addressId: Int? = null,
    @SerializedName("shippingName")
    val shippingName: String? = null,
    @SerializedName("shippingPhone")
    val shippingPhone: String? = null,
    @SerializedName("shippingCity")
    val shippingCity: String? = null,
    @SerializedName("shippingDistrict")
    val shippingDistrict: String? = null,
    @SerializedName("shippingAddress")
    val shippingAddress: String? = null,
    @SerializedName("shippingZipCode")
    val shippingZipCode: String? = null,
    @SerializedName("customerNote")
    val customerNote: String? = null
)