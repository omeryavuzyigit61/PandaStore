package com.allmoviedatabase.pandastore.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.allmoviedatabase.pandastore.databinding.ItemCartBinding
import com.allmoviedatabase.pandastore.model.cart.CartItemDto
import com.allmoviedatabase.pandastore.util.toCurrency
import com.bumptech.glide.Glide

class CartAdapter(
    private val onIncreaseClick: (CartItemDto) -> Unit,
    private val onDecreaseClick: (CartItemDto) -> Unit,
    private val onDeleteClick: (CartItemDto) -> Unit
) : ListAdapter<CartItemDto, CartAdapter.CartViewHolder>(CartDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CartViewHolder {
        val binding = ItemCartBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CartViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CartViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class CartViewHolder(private val binding: ItemCartBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: CartItemDto) {
            // Bilgiler
            binding.tvCartProductName.text = item.product.name
            binding.tvQuantity.text = item.quantity.toString()

            // Fiyat (Anlık fiyat * adet mi yoksa birim fiyat mı? API item.priceAtAdd dönüyor)
            // Eğer toplam satır fiyatı göstermek istersen: (item.priceAtAdd * item.quantity)
            binding.tvCartPrice.text = item.priceAtAdd.toCurrency()

            // Görsel
            Glide.with(itemView)
                .load(item.product.getThumbnailUrl()) // Senin ProductDto'da bu metod vardı
                .placeholder(android.R.drawable.ic_menu_gallery)
                .into(binding.imgCartProduct)

            // Tıklama Olayları
            binding.btnIncrease.setOnClickListener { onIncreaseClick(item) }

            binding.btnDecrease.setOnClickListener {
                if (item.quantity > 1) {
                    onDecreaseClick(item)
                } else {
                    // Miktar 1 ise eksiye basınca silinsin mi? Genelde silme butonu ayrıdır.
                    // Şimdilik işlem yapmayalım.
                }
            }

            binding.btnDeleteCartItem.setOnClickListener { onDeleteClick(item) }
        }
    }

    class CartDiffCallback : DiffUtil.ItemCallback<CartItemDto>() {
        override fun areItemsTheSame(oldItem: CartItemDto, newItem: CartItemDto) = oldItem.id == newItem.id
        override fun areContentsTheSame(oldItem: CartItemDto, newItem: CartItemDto) = oldItem == newItem
    }
}