package com.allmoviedatabase.pandastore.adapter

import android.graphics.Paint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.allmoviedatabase.pandastore.databinding.ItemProductBinding
import com.allmoviedatabase.pandastore.model.product.ProductDto
import com.bumptech.glide.Glide
import com.allmoviedatabase.pandastore.util.toCurrency

class ProductAdapter(
    private val onProductClick: (ProductDto) -> Unit,
    private val onAddToCartClick: (ProductDto) -> Unit,
    private val onFavoriteClick: (ProductDto) -> Unit // YENİ: Favori Tıklama
) : ListAdapter<ProductDto, ProductAdapter.ProductViewHolder>(ProductDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
        val binding = ItemProductBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ProductViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class ProductViewHolder(private val binding: ItemProductBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(product: ProductDto) {
            binding.tvProductName.text = product.name
            binding.tvBrand.text = product.brand ?: ""
            binding.tvPrice.text = product.price.toCurrency()

            Glide.with(binding.root.context)
                .load(product.getThumbnailUrl())
                .into(binding.imgProduct)

            // İndirim Gösterimi
            if (product.discountRate != null && product.discountRate > 0) {
                binding.layoutDiscount.visibility = View.VISIBLE
                binding.tvDiscountPercent.text = "%${product.discountRate}"
                if (product.compareAtPrice != null) {
                    binding.tvOldPrice.text = product.compareAtPrice.toCurrency()
                    binding.tvOldPrice.visibility = View.VISIBLE
                    binding.tvOldPrice.paintFlags = binding.tvOldPrice.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
                }
            } else {
                binding.layoutDiscount.visibility = View.GONE
            }

            // TIKLAMA OLAYLARI
            binding.root.setOnClickListener { onProductClick(product) }
            binding.btnAddCart.setOnClickListener { onAddToCartClick(product) }

            // YENİ: Kalp ikonuna tıklama
            binding.btnFavoriteAction.setOnClickListener { onFavoriteClick(product) }
        }
    }

    class ProductDiffCallback : DiffUtil.ItemCallback<ProductDto>() {
        override fun areItemsTheSame(oldItem: ProductDto, newItem: ProductDto) = oldItem.id == newItem.id
        override fun areContentsTheSame(oldItem: ProductDto, newItem: ProductDto) = oldItem == newItem
    }
}