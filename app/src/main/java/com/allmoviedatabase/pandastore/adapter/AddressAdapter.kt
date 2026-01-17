package com.allmoviedatabase.pandastore.view.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.allmoviedatabase.pandastore.databinding.ItemAddressBinding
import com.allmoviedatabase.pandastore.model.address.AddressDto

class AddressAdapter(
    private val onEditClick: (AddressDto) -> Unit,
    private val onDeleteClick: (AddressDto) -> Unit
) : ListAdapter<AddressDto, AddressAdapter.AddressViewHolder>(AddressDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AddressViewHolder {
        val binding = ItemAddressBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return AddressViewHolder(binding)
    }

    override fun onBindViewHolder(holder: AddressViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class AddressViewHolder(private val binding: ItemAddressBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(address: AddressDto) {
            binding.tvTitle.text = address.title
            binding.tvAddress.text = address.openAddress
            binding.tvCity.text = "${address.district} / ${address.city}"

            binding.btnEdit.setOnClickListener { onEditClick(address) }
            binding.btnDelete.setOnClickListener { onDeleteClick(address) }
        }
    }

    class AddressDiffCallback : DiffUtil.ItemCallback<AddressDto>() {
        override fun areItemsTheSame(oldItem: AddressDto, newItem: AddressDto) = oldItem.id == newItem.id
        override fun areContentsTheSame(oldItem: AddressDto, newItem: AddressDto) = oldItem == newItem
    }
}