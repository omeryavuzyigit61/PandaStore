package com.allmoviedatabase.pandastore.repository

import com.allmoviedatabase.pandastore.data.remote.ApiService
import com.allmoviedatabase.pandastore.model.address.AddressRequest
import jakarta.inject.Inject

class AddressRepositoryImpl @Inject constructor(
    private val apiService: ApiService
) : AddressRepository {

    override fun getAddresses() = apiService.getAddresses()

    override fun addAddress(request: AddressRequest) = apiService.addAddress(request)

    override fun updateAddress(id: Int, request: AddressRequest) = apiService.updateAddress(id, request)

    override fun deleteAddress(id: Int) = apiService.deleteAddress(id)
}