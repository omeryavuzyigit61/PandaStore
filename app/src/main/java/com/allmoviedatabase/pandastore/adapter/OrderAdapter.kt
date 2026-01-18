package com.allmoviedatabase.pandastore.adapter

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.allmoviedatabase.pandastore.databinding.ItemOrderBinding
import com.allmoviedatabase.pandastore.model.order.OrderDto
import com.allmoviedatabase.pandastore.util.toCurrency
import com.bumptech.glide.Glide

class OrderAdapter(
    private val onOrderClick: (OrderDto) -> Unit
) : ListAdapter<OrderDto, OrderAdapter.OrderViewHolder>(OrderDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderViewHolder {
        val binding = ItemOrderBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return OrderViewHolder(binding)
    }

    override fun onBindViewHolder(holder: OrderViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class OrderViewHolder(private val binding: ItemOrderBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: OrderDto) {
            binding.tvOrderNumber.text = "Sipariş #${item.orderNumber}"
            binding.tvOrderDate.text = item.createdAt.take(10) // Basit tarih gösterimi (YYYY-MM-DD)
            binding.tvOrderTotal.text = item.total.toCurrency()

            // Durum Rengi ve Metni (Türkçeleştirme)
            val (statusText, colorCode) = when(item.status) {
                "pending" -> "Beklemede" to "#FFA000" // Turuncu
                "confirmed" -> "Onaylandı" to "#1976D2" // Mavi
                "shipped" -> "Kargoda" to "#7B1FA2" // Mor
                "delivered" -> "Teslim Edildi" to "#388E3C" // Yeşil
                "cancelled" -> "İptal" to "#D32F2F" // Kırmızı
                else -> item.status to "#757575"
            }
            binding.tvOrderStatus.text = statusText
            binding.tvOrderStatus.setTextColor(Color.parseColor(colorCode))

            // Ürün Özeti
            if (item.items.isNotEmpty()) {
                val firstItem = item.items[0]
                Glide.with(itemView).load(firstItem.productImage).into(binding.imgProductThumb)

                val moreCount = item.items.size - 1
                binding.tvProductSummary.text = if (moreCount > 0) {
                    "${firstItem.productName} ve $moreCount ürün daha"
                } else {
                    firstItem.productName
                }
            }

            binding.root.setOnClickListener { onOrderClick(item) }
        }
    }

    class OrderDiffCallback : DiffUtil.ItemCallback<OrderDto>() {
        override fun areItemsTheSame(oldItem: OrderDto, newItem: OrderDto) = oldItem.id == newItem.id
        override fun areContentsTheSame(oldItem: OrderDto, newItem: OrderDto) = oldItem == newItem
    }
}