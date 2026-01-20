package com.allmoviedatabase.pandastore.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.allmoviedatabase.pandastore.databinding.ItemSimpleListSelectionBinding
import com.allmoviedatabase.pandastore.model.lists.CustomListDto

/**
 * Ürün detayında "Listeye Ekle" dendiğinde açılan
 * BottomSheet içindeki listeleri gösteren basit adapter.
 */
class SelectListAdapter(
    private val lists: List<CustomListDto>,
    private val onListSelected: (CustomListDto) -> Unit
) : RecyclerView.Adapter<SelectListAdapter.SelectListViewHolder>() {

    inner class SelectListViewHolder(val binding: ItemSimpleListSelectionBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: CustomListDto) {
            binding.tvListName.text = "${item.icon} ${item.name}"
            binding.root.setOnClickListener {
                onListSelected(item)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SelectListViewHolder {
        val binding = ItemSimpleListSelectionBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return SelectListViewHolder(binding)
    }

    override fun onBindViewHolder(holder: SelectListViewHolder, position: Int) {
        holder.bind(lists[position])
    }

    override fun getItemCount(): Int = lists.size
}