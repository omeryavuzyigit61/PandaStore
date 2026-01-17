package com.allmoviedatabase.pandastore.view.fragment

import android.graphics.Paint
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.allmoviedatabase.pandastore.R
import com.allmoviedatabase.pandastore.adapter.AttributeAdapter
import com.allmoviedatabase.pandastore.adapter.ReviewAdapter
import com.allmoviedatabase.pandastore.databinding.FragmentProductDetailBinding
import com.allmoviedatabase.pandastore.model.product.ProductDto
import com.allmoviedatabase.pandastore.util.toCurrency
import com.allmoviedatabase.pandastore.viewmodel.ProductDetailViewModel
import com.bumptech.glide.Glide
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ProductDetailFragment : Fragment(R.layout.fragment_product_detail) {

    private lateinit var binding: FragmentProductDetailBinding
    private val args: ProductDetailFragmentArgs by navArgs()

    private val viewModel: ProductDetailViewModel by viewModels()

    private val attributeAdapter = AttributeAdapter()
    private val reviewAdapter = ReviewAdapter()

    private var myExistingReviewId: Int? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentProductDetailBinding.bind(view)

        val product = args.product

        // UI Başlat
        setupUI(product)
        setupListeners(product.id)

        // Verileri Çek
        viewModel.getProductDetail(product.id)
        viewModel.getReviews(product.id)

        // Dinle
        observeViewModel()
    }

    private fun observeViewModel() {
        // 1. Ürün Detayını Dinle
        viewModel.productDetail.observe(viewLifecycleOwner) { fullProduct ->
            setupUI(fullProduct)
        }

        // Listede benim yorumum var mı kontrol et
        viewModel.reviews.observe(viewLifecycleOwner) { reviewList ->
            if (reviewList.isNotEmpty()) {
                binding.rvReviews.visibility = View.VISIBLE
                binding.tvNoReviews.visibility = View.GONE
                reviewAdapter.submitList(reviewList)
            } else {
                binding.rvReviews.visibility = View.GONE
                binding.tvNoReviews.visibility = View.VISIBLE
            }

            // ARTIK PARAMETRE GÖNDERMİYORUZ. ViewModel kendi hallediyor.
            viewModel.checkMyReview(reviewList)
        }

        // 3. Benim Yorumum Var mı? (checkMyReview sonucu)
        viewModel.myReview.observe(viewLifecycleOwner) { myReview ->
            if (myReview != null) {
                // --- GÜNCELLEME MODU ---
                myExistingReviewId = myReview.id

                binding.btnSendReview.text = "GÜNCELLE"
                binding.btnDeleteReview.visibility = View.VISIBLE

                // Mevcut verileri doldur
                binding.ratingBarInput.rating = myReview.rating.toFloat()
                binding.etReviewTitle.setText(myReview.title)
                binding.etComment.setText(myReview.comment)
            } else {
                // --- EKLEME MODU ---
                myExistingReviewId = null

                binding.btnSendReview.text = "GÖNDER"
                binding.btnDeleteReview.visibility = View.GONE

                // Alanları temizle (Eğer kullanıcı sildiyse temizlensin)
                if (binding.etReviewTitle.text.toString().isNotEmpty()) {
                    binding.ratingBarInput.rating = 0f
                    binding.etReviewTitle.text?.clear()
                    binding.etComment.text?.clear()
                }
            }
        }

        // 4. İşlem Başarısı (Ekleme/Silme/Güncelleme sonrası)
        viewModel.reviewActionSuccess.observe(viewLifecycleOwner) { message ->
            if (message != null) {
                Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
                // Alanları temizleme işini myReview observer'a bıraktık
            }
        }

        // 5. Hatalar
        viewModel.error.observe(viewLifecycleOwner) { error ->
            if (!error.isNullOrEmpty()) {
                Toast.makeText(context, error, Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun setupUI(product: ProductDto) {
        // Görsel
        Glide.with(this)
            .load(product.getThumbnailUrl())
            .placeholder(android.R.drawable.ic_menu_gallery)
            .into(binding.imgProductDetail)

        // Bilgiler
        binding.tvDetailBrand.text = product.brand ?: ""
        binding.tvDetailName.text = product.name
        binding.tvRating.text = "${product.averageRating} (${product.reviewCount})"
        binding.ratingBarSummary.rating = product.averageRating.toFloat()

        // Fiyat
        binding.tvBottomPrice.text = product.price.toCurrency()

        if (product.compareAtPrice != null && product.compareAtPrice > product.price) {
            binding.layoutOldPrice.visibility = View.VISIBLE
            binding.tvDetailOldPrice.text = product.compareAtPrice.toCurrency()
            binding.tvDetailOldPrice.paintFlags = binding.tvDetailOldPrice.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG

            if (product.discountRate != null && product.discountRate > 0) {
                binding.tvDetailDiscount.visibility = View.VISIBLE
                binding.tvDetailDiscount.text = "%${product.discountRate}"
            } else {
                binding.tvDetailDiscount.visibility = View.GONE
            }
        } else {
            binding.layoutOldPrice.visibility = View.GONE
        }

        // Açıklama
        binding.tvDescription.text = if (!product.description.isNullOrEmpty()) product.description else "Detaylar yükleniyor..."

        // Özellikler
        binding.rvAttributes.layoutManager = LinearLayoutManager(context)
        binding.rvAttributes.adapter = attributeAdapter

        if (!product.attributeValues.isNullOrEmpty()) {
            binding.lblAttributes.visibility = View.VISIBLE
            binding.rvAttributes.visibility = View.VISIBLE
            attributeAdapter.submitList(product.attributeValues)
        } else {
            binding.lblAttributes.visibility = View.GONE
            binding.rvAttributes.visibility = View.GONE
        }

        // Yorum Listesi Ayarı
        binding.rvReviews.layoutManager = LinearLayoutManager(context)
        binding.rvReviews.adapter = reviewAdapter
    }

    private fun setupListeners(productId: Int) {
        binding.btnBack.setOnClickListener { findNavController().popBackStack() }

        binding.btnAddToCart.setOnClickListener {
            Toast.makeText(context, "Sepete Eklendi", Toast.LENGTH_SHORT).show()
        }

        // --- SİLME BUTONU ---
        binding.btnDeleteReview.setOnClickListener {
            myExistingReviewId?.let { reviewId ->
                viewModel.deleteReview(reviewId, productId)
            }
        }

        // --- GÖNDER / GÜNCELLE BUTONU ---
        binding.btnSendReview.setOnClickListener {
            val rating = binding.ratingBarInput.rating.toInt()
            val title = binding.etReviewTitle.text.toString().trim()
            val comment = binding.etComment.text.toString().trim()

            // Validasyon
            if (rating == 0) {
                Toast.makeText(context, "Lütfen puan verin", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (title.isEmpty()) {
                binding.etReviewTitle.error = "Başlık gerekli"
                return@setOnClickListener
            }
            if (comment.isEmpty()) {
                binding.etComment.error = "Yorum gerekli"
                return@setOnClickListener
            }

            // Karar Anı: Güncelleme mi, Yeni Kayıt mı?
            if (myExistingReviewId != null) {
                // GÜNCELLE
                viewModel.updateReview(myExistingReviewId!!, productId, rating, title, comment)
            } else {
                // YENİ EKLE
                viewModel.submitReview(productId, rating, title, comment)
            }
        }

        binding.btnSeeAllReviews.setOnClickListener {
            Toast.makeText(context, "Tüm Yorumlar Sayfası (Yakında)", Toast.LENGTH_SHORT).show()
        }
    }
}