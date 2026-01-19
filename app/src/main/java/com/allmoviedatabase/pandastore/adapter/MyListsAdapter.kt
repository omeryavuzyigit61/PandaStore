package com.allmoviedatabase.pandastore.adapter

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.allmoviedatabase.pandastore.databinding.ItemCustomListBinding
import com.allmoviedatabase.pandastore.model.lists.CustomListDto

class MyListsAdapter(
    private val onListClick: (CustomListDto) -> Unit,
    private val onDeleteClick: (CustomListDto) -> Unit
) : ListAdapter<CustomListDto, MyListsAdapter.ListViewHolder>(ListDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListViewHolder {
        val binding = ItemCustomListBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ListViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ListViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class ListViewHolder(private val binding: ItemCustomListBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: CustomListDto) {
            binding.tvListName.text = item.name
            binding.tvListIcon.text = item.icon ?: "📁"

            val visibility = if (item.isPrivate) "Gizli" else "Herkese Açık"
            // Item count null gelebilir, 0 varsayalım
            val count = item.items?.size ?: item.itemCount ?: 0
            binding.tvListInfo.text = "$count Ürün • $visibility"

            // Renk Ayarı (Hex string to Color)
            try {
                val color = Color.parseColor(item.coverColor ?: "#E0E0E0")
                binding.cardIcon.setCardBackgroundColor(color)
            } catch (e: Exception) {
                binding.cardIcon.setCardBackgroundColor(Color.LTGRAY)
            }

            binding.root.setOnClickListener { onListClick(item) }
            binding.btnDeleteList.setOnClickListener { onDeleteClick(item) }
        }
    }

    class ListDiffCallback : DiffUtil.ItemCallback<CustomListDto>() {
        override fun areItemsTheSame(oldItem: CustomListDto, newItem: CustomListDto) = oldItem.id == newItem.id
        override fun areContentsTheSame(oldItem: CustomListDto, newItem: CustomListDto) = oldItem == newItem
    }
}