package com.allmoviedatabase.pandastore.model.lists

data class UpdateListRequest(
    val name: String,
    val isPrivate: Boolean
)