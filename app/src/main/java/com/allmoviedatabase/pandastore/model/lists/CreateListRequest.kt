package com.allmoviedatabase.pandastore.model.lists

data class CreateListRequest(
    val name: String,
    val description: String? = null,
    val isPrivate: Boolean = true,
    val coverColor: String? = "#000000",
    val icon: String? = "📁"
)