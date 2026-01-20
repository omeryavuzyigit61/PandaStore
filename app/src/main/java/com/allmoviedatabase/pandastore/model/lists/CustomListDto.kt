package com.allmoviedatabase.pandastore.model.lists

import android.os.Parcelable
import com.google.gson.annotations.SerializedName

data class CustomListDto(
    val id: Int,
    val name: String,
    val description: String?,
    val isPrivate: Boolean,
    val coverColor: String?, // Hex code: #6366f1
    val icon: String?, // Emoji: 🎁
    val itemCount: Int? = 0, // Bazı API'ler liste çekerken item sayısını da döner, dönmezse items.size kullanırız
    val createdAt: String,
    @SerializedName("items")
    val products: List<ListItemDto>? = null
)