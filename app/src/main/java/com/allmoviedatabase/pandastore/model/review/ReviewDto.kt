package com.allmoviedatabase.pandastore.model.review

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class ReviewDto(
    val id: Int,
    @SerializedName("userId")
    val userId: String,
    val rating: Int,
    val title: String?,   // Başlık (Opsiyonel olabilir)
    val comment: String?, // Yorum (Opsiyonel olabilir)
    val isVerifiedPurchase: Boolean, // Satın almış mı?
    val createdAt: String, // Tarih "2026-01-16T..."

    // API'de user objesi iç içe geliyor:
    @SerializedName("user")
    val user: ReviewUserDto
) : Parcelable