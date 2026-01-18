package com.allmoviedatabase.pandastore.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.allmoviedatabase.pandastore.databinding.ItemSavedAddressBinding
import com.allmoviedatabase.pandastore.model.address.AddressDto

class SavedAddressAdapter(
    private val onAddressClick: (AddressDto) -> Unit
) : ListAdapter<AddressDto, SavedAddressAdapter.AddressViewHolder>(AddressDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AddressViewHolder {
        val binding = ItemSavedAddressBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return AddressViewHolder(binding)
    }

    override fun onBindViewHolder(holder: AddressViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class AddressViewHolder(private val binding: ItemSavedAddressBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(address: AddressDto) {
            binding.tvAddressTitle.text = address.title
            binding.tvAddressCity.text = "${address.city} / ${address.district}"

            binding.root.setOnClickListener {
                onAddressClick(address)
            }
        }
    }

    class AddressDiffCallback : DiffUtil.ItemCallback<AddressDto>() {
        override fun areItemsTheSame(oldItem: AddressDto, newItem: AddressDto) = oldItem.id == newItem.id
        override fun areContentsTheSame(oldItem: AddressDto, newItem: AddressDto) = oldItem == newItem
    }
}