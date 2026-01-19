package com.allmoviedatabase.pandastore.model.lists

data class AddListItemRequest(
    val productId: Int,
    val note: String? = null
)