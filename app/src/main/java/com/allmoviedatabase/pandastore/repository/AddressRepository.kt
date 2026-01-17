package com.allmoviedatabase.pandastore.repository

import com.allmoviedatabase.pandastore.model.address.AddressDto
import com.allmoviedatabase.pandastore.model.address.AddressRequest
import io.reactivex.rxjava3.core.Single

interface AddressRepository {
    fun getAddresses(): Single<List<AddressDto>>
    fun addAddress(request: AddressRequest): Single<AddressDto>
    fun updateAddress(id: Int, request: AddressRequest): Single<AddressDto>
    fun deleteAddress(id: Int): Single<Unit>
}