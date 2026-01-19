package com.allmoviedatabase.pandastore.repository

import com.allmoviedatabase.pandastore.model.lists.CreateListRequest
import com.allmoviedatabase.pandastore.model.lists.CustomListDto
import io.reactivex.rxjava3.core.Single

interface ListRepository {
    fun getMyLists(): Single<List<CustomListDto>>
    fun createList(request: CreateListRequest): Single<CustomListDto>
    fun getListDetail(id: Int): Single<CustomListDto>
    fun deleteList(id: Int): Single<Any>
    fun updateList(id: Int, name: String, isPrivate: Boolean): Single<CustomListDto>
    fun getPublicLists(): Single<List<CustomListDto>> // ViewModel direkt List istiyor
    fun addProductToList(listId: Int, productId: Int, note: String?): Single<Any>
    fun removeProductFromList(listId: Int, productId: Int): Single<Any>
}