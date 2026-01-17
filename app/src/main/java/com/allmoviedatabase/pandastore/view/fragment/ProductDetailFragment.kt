package com.allmoviedatabase.pandastore.view

import android.graphics.Paint
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.allmoviedatabase.pandastore.R
import com.allmoviedatabase.pandastore.databinding.FragmentProductDetailBinding
import com.allmoviedatabase.pandastore.util.toCurrency
import com.allmoviedatabase.pandastore.view.adapter.AttributeAdapter
import com.bumptech.glide.Glide
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ProductDetailFragment : Fragment(R.layout.fragment_product_detail) {

    private lateinit var binding: FragmentProductDetailBinding
    private val args: ProductDetailFragmentArgs by navArgs() // Navigation Arguments
    private val attributeAdapter = AttributeAdapter()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentProductDetailBinding.bind(view)

        val product = args.product // HomeFragment'tan gelen ürün

        setupUI(product)
    }

    private fun setupUI(product: com.allmoviedatabase.pandastore.model.product.ProductDto) {
        // 1. Görsel
        Glide.with(this)
            .load(product.getThumbnailUrl())
            .placeholder(android.R.drawable.ic_menu_gallery)
            .into(binding.imgProductDetail)

        // 2. Metin Bilgileri
        binding.tvDetailBrand.text = product.brand ?: "Genel"
        binding.tvDetailName.text = product.name
        binding.tvDescription.text = "Bu ürün hakkında detaylı açıklama burada yer alacak. (API'den description gelirse buraya bağlanacak)."

        // Puanlama
        binding.tvRating.text = "${product.averageRating} (${product.reviewCount} Değerlendirme)"

        // 3. Fiyat
        binding.tvBottomPrice.text = product.price.toCurrency()

        // 4. Özellikler Listesi
        binding.rvAttributes.layoutManager = LinearLayoutManager(context)
        binding.rvAttributes.adapter = attributeAdapter

        if (!product.attributeValues.isNullOrEmpty()) {
            attributeAdapter.submitList(product.attributeValues)
        } else {

        }

        // 5. Butonlar
        binding.btnBack.setOnClickListener {
            findNavController().popBackStack()
        }

        binding.btnAddToCart.setOnClickListener {
            Toast.makeText(context, "${product.name} Sepete Eklendi!", Toast.LENGTH_SHORT).show()
        }
    }
}