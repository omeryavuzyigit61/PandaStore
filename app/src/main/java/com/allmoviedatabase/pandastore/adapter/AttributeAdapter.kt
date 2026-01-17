package com.allmoviedatabase.pandastore.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.allmoviedatabase.pandastore.databinding.ItemAttributeBinding
import com.allmoviedatabase.pandastore.model.product.ProductAttributeValueDto

class AttributeAdapter : ListAdapter<ProductAttributeValueDto, AttributeAdapter.AttributeViewHolder>(AttributeDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AttributeViewHolder {
        val binding = ItemAttributeBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return AttributeViewHolder(binding)
    }

    override fun onBindViewHolder(holder: AttributeViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class AttributeViewHolder(private val binding: ItemAttributeBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: ProductAttributeValueDto) {
            // JSON'dan gelen "attributeName": "RAM" -> Başlık
            binding.tvAttributeName.text = item.attributeName

            // JSON'dan gelen "value": "8GB" -> Değer
            binding.tvAttributeValue.text = item.value
        }
    }

    class AttributeDiffCallback : DiffUtil.ItemCallback<ProductAttributeValueDto>() {
        override fun areItemsTheSame(oldItem: ProductAttributeValueDto, newItem: ProductAttributeValueDto): Boolean {
            // ID varsa ID kullan, yoksa isimden kontrol et
            return oldItem.attributeId == newItem.attributeId
        }

        override fun areContentsTheSame(oldItem: ProductAttributeValueDto, newItem: ProductAttributeValueDto): Boolean {
            return oldItem == newItem
        }
    }
}