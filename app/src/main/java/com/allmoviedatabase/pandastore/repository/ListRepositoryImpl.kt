package com.allmoviedatabase.pandastore.repository

import com.allmoviedatabase.pandastore.data.remote.ApiService
import com.allmoviedatabase.pandastore.model.lists.AddListItemRequest
import com.allmoviedatabase.pandastore.model.lists.CreateListRequest
import com.allmoviedatabase.pandastore.model.lists.CustomListDto
import com.allmoviedatabase.pandastore.model.lists.UpdateListRequest
import io.reactivex.rxjava3.core.Single
import jakarta.inject.Inject

class ListRepositoryImpl @Inject constructor(
    private val api: ApiService
) : ListRepository {

    override fun getMyLists(): Single<List<CustomListDto>> = api.getMyLists()

    override fun createList(request: CreateListRequest): Single<CustomListDto> = api.createList(request)

    override fun getListDetail(id: Int): Single<CustomListDto> = api.getListDetail(id)

    override fun deleteList(id: Int): Single<Any> = api.deleteList(id)

    override fun updateList(id: Int, name: String, isPrivate: Boolean): Single<CustomListDto> {
        val request = UpdateListRequest(name, isPrivate)
        return api.updateList(id, request)
    }

    override fun getPublicLists(): Single<List<CustomListDto>> {
        // API bize { data: [...], meta: ... } dönüyor.
        // Biz sadece data (List) kısmını alıp ViewModel'e veriyoruz.
        return api.getDiscoverLists().map { response -> response.data }
    }

    override fun addProductToList(listId: Int, productId: Int, note: String?): Single<Any> {
        return api.addProductToList(listId, AddListItemRequest(productId, note))
    }

    override fun removeProductFromList(listId: Int, productId: Int): Single<Any> {
        return api.removeProductFromList(listId, productId)
    }
}