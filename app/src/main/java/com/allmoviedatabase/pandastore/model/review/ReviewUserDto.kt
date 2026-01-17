package com.allmoviedatabase.pandastore.model.review

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class ReviewUserDto(
    val firstName: String,
    val lastName: String
) : Parcelable {
    // UI'da "Ömer Y." şeklinde göstermek için yardımcı fonksiyon
    fun getFullName(): String {
        return "$firstName $lastName"
    }
}