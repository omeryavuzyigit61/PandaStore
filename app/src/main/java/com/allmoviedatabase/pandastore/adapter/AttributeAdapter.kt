package com.allmoviedatabase.pandastore.view.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.allmoviedatabase.pandastore.databinding.ItemAttributeBinding
import com.allmoviedatabase.pandastore.model.product.ProductAttributeValueDto

class AttributeAdapter : RecyclerView.Adapter<AttributeAdapter.AttributeViewHolder>() {

    private var items: List<ProductAttributeValueDto> = emptyList()

    fun submitList(newItems: List<ProductAttributeValueDto>) {
        items = newItems
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AttributeViewHolder {
        val binding = ItemAttributeBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return AttributeViewHolder(binding)
    }

    override fun onBindViewHolder(holder: AttributeViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount() = items.size

    class AttributeViewHolder(private val binding: ItemAttributeBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: ProductAttributeValueDto) {
            binding.tvAttributeName.text = item.attributeName
            binding.tvAttributeValue.text = item.value
        }
    }
}