package com.allmoviedatabase.pandastore.model.product

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class ProductAttributeValueDto(
    val attributeName: String, // Örn: RAM
    val value: String          // Örn: 8GB
) : Parcelable