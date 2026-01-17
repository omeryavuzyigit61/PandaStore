package com.allmoviedatabase.pandastore.model.product

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class ProductImageDto(
    val id: Int,
    val url: String,
    val isPrimary: Boolean
) : Parcelable