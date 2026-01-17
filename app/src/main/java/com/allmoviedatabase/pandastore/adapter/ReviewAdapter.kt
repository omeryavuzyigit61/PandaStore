package com.allmoviedatabase.pandastore.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.allmoviedatabase.pandastore.databinding.ItemReviewBinding
import com.allmoviedatabase.pandastore.model.review.ReviewDto

class ReviewAdapter : ListAdapter<ReviewDto, ReviewAdapter.ReviewViewHolder>(ReviewDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReviewViewHolder {
        val binding = ItemReviewBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ReviewViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ReviewViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class ReviewViewHolder(private val binding: ItemReviewBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: ReviewDto) {
            // 1. Kullanıcı Adı (ReviewDto içindeki user objesinden)
            // Eğer modelinde user yoksa item.userName gibi düzeltmelisin.
            // API dökümanına göre: item.user.firstName
            val fullName = "${item.user.firstName} ${item.user.lastName}"
            binding.tvUserName.text = fullName

            // 2. Yorum Metni
            binding.tvComment.text = item.comment

            // 3. Tarih (Sadece ilk 10 karakter: YYYY-MM-DD)
            binding.tvDate.text = item.createdAt.take(10)

            // 4. Puan (RatingBar)
            binding.ratingBarReview.rating = item.rating.toFloat()

            // 5. Avatar Harfi (İsmin baş harfi)
            val firstChar = item.user.firstName.firstOrNull()?.toString()?.uppercase() ?: "?"
            binding.tvAvatarText.text = firstChar
        }
    }

    class ReviewDiffCallback : DiffUtil.ItemCallback<ReviewDto>() {
        override fun areItemsTheSame(oldItem: ReviewDto, newItem: ReviewDto) = oldItem.id == newItem.id
        override fun areContentsTheSame(oldItem: ReviewDto, newItem: ReviewDto) = oldItem == newItem
    }
}