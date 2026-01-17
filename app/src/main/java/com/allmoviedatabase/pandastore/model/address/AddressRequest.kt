package com.allmoviedatabase.pandastore.model.address

data class AddressRequest(
    val title: String?,
    val city: String?,
    val district: String?,
    val openAddress: String?,
    val zipCode: String?
)
