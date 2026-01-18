package com.allmoviedatabase.pandastore.model.address

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class AddressDto(
    val id: Int,
    val title: String, // Örn: "Evim", "İş Yeri"
    val name: String,
    val phone: String,
    val city: String,
    val district: String,
    val openAddress: String,
    val zipCode: String?
) : Parcelable
