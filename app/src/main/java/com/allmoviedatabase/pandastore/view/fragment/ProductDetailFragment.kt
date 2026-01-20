package com.allmoviedatabase.pandastore.view.fragment

import android.graphics.Paint
import android.os.Bundle
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.allmoviedatabase.pandastore.R
import com.allmoviedatabase.pandastore.adapter.AttributeAdapter
import com.allmoviedatabase.pandastore.adapter.ReviewAdapter
import com.allmoviedatabase.pandastore.adapter.SelectListAdapter
import com.allmoviedatabase.pandastore.databinding.FragmentProductDetailBinding
import com.allmoviedatabase.pandastore.model.product.ProductDto
import com.allmoviedatabase.pandastore.util.toCurrency
import com.allmoviedatabase.pandastore.viewmodel.ProductDetailViewModel
import com.bumptech.glide.Glide
import com.google.android.material.bottomsheet.BottomSheetDialog
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

        setupUI(product)
        setupListeners(product.id)

        // Veri Çekme Çağrıları
        viewModel.getProductDetail(product.id)
        viewModel.getReviews(product.id)
        viewModel.checkFavoriteStatus(product.id) // Favori durumu kontrolü

        observeViewModel()
    }

    private fun observeViewModel() {
        // 1. Ürün Detay
        viewModel.productDetail.observe(viewLifecycleOwner) { fullProduct ->
            setupUI(fullProduct)
        }

        // 2. Yorumlar & Benim Yorumum
        viewModel.reviews.observe(viewLifecycleOwner) { reviewList ->
            if (reviewList.isNotEmpty()) {
                binding.rvReviews.visibility = View.VISIBLE
                binding.tvNoReviews.visibility = View.GONE
                reviewAdapter.submitList(reviewList)
            } else {
                binding.rvReviews.visibility = View.GONE
                binding.tvNoReviews.visibility = View.VISIBLE
            }
            viewModel.checkMyReview(reviewList)
        }

        viewModel.myReview.observe(viewLifecycleOwner) { myReview ->
            if (myReview != null) {
                myExistingReviewId = myReview.id
                binding.btnSendReview.text = "GÜNCELLE"
                binding.btnDeleteReview.visibility = View.VISIBLE
                binding.ratingBarInput.rating = myReview.rating.toFloat()
                binding.etReviewTitle.setText(myReview.title)
                binding.etComment.setText(myReview.comment)
            } else {
                myExistingReviewId = null
                binding.btnSendReview.text = "GÖNDER"
                binding.btnDeleteReview.visibility = View.GONE
                if (binding.etReviewTitle.text.toString().isNotEmpty()) {
                    binding.ratingBarInput.rating = 0f
                    binding.etReviewTitle.text?.clear()
                    binding.etComment.text?.clear()
                }
            }
        }

        // 3. FAVORİ DURUMU (YENİ)
        viewModel.isFavorite.observe(viewLifecycleOwner) { isFav ->
            if (isFav) {
                binding.imgFavoriteIcon.setImageResource(R.drawable.heart) // Dolu kalp
            } else {
                binding.imgFavoriteIcon.setImageResource(R.drawable.ic_heart_border) // Boş kalp
            }
        }

        // 4. LİSTELER GELDİĞİNDE BOTTOM SHEET AÇ (YENİ)
        viewModel.userLists.observe(viewLifecycleOwner) { lists ->
            // Eğer listeler geldiyse sheet'i aç
            // Not: Bu tetikleme her liste çekildiğinde olur, butona basıldığını kontrol etmek daha sağlam olabilir
            // ama şimdilik butona basınca fetchUserLists çağırdığımız için burada açıyoruz.
            showSelectListBottomSheet(args.product.id, lists)
        }

        // 5. BAŞARI / HATA MESAJLARI
        viewModel.reviewActionSuccess.observe(viewLifecycleOwner) { message ->
            if (message != null) {
                Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
                viewModel.clearMessages()
            }
        }
        viewModel.addToCartSuccess.observe(viewLifecycleOwner) { message ->
            if (message != null) {
                Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
                viewModel.clearAddToCartMessage()
            }
        }
        viewModel.error.observe(viewLifecycleOwner) { error ->
            if (!error.isNullOrEmpty()) {
                Toast.makeText(context, error, Toast.LENGTH_LONG).show()
                viewModel.clearMessages()
            }
        }
    }

    private fun setupListeners(productId: Int) {
        binding.btnBack.setOnClickListener { findNavController().popBackStack() }

        // Sepet
        binding.btnAddToCart.setOnClickListener {
            viewModel.addToCart(productId, quantity = 1)
        }

        // --- FAVORİ TIKLAMA ---
        binding.btnFavorite.setOnClickListener {
            viewModel.toggleFavorite(productId)
        }

        // --- LİSTEYE KAYDET TIKLAMA ---
        binding.btnSaveToList.setOnClickListener {
            viewModel.fetchUserLists() // Listeleri çek, observe metodu sheet'i açacak
        }

        // Yorum İşlemleri
        binding.btnDeleteReview.setOnClickListener {
            myExistingReviewId?.let { reviewId -> viewModel.deleteReview(reviewId, productId) }
        }

        binding.btnSendReview.setOnClickListener {
            val rating = binding.ratingBarInput.rating.toInt()
            val title = binding.etReviewTitle.text.toString().trim()
            val comment = binding.etComment.text.toString().trim()

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

            if (myExistingReviewId != null) {
                viewModel.updateReview(myExistingReviewId!!, productId, rating, title, comment)
            } else {
                viewModel.submitReview(productId, rating, title, comment)
            }
        }

        binding.btnSeeAllReviews.setOnClickListener {
            Toast.makeText(context, "Tüm Yorumlar Sayfası (Yakında)", Toast.LENGTH_SHORT).show()
        }
    }

    // BOTTOM SHEET: LİSTE SEÇİMİ
    private fun showSelectListBottomSheet(productId: Int, lists: List<com.allmoviedatabase.pandastore.model.lists.CustomListDto>) {
        val dialog = BottomSheetDialog(requireContext())
        val view = layoutInflater.inflate(R.layout.bottom_sheet_select_list, null)
        dialog.setContentView(view)

        val rvSelectLists = view.findViewById<RecyclerView>(R.id.rvSelectLists)
        val tvEmpty = view.findViewById<TextView>(R.id.tvEmptyListWarning)

        if (lists.isEmpty()) {
            tvEmpty.visibility = View.VISIBLE
            rvSelectLists.visibility = View.GONE
        } else {
            tvEmpty.visibility = View.GONE
            rvSelectLists.visibility = View.VISIBLE
            rvSelectLists.layoutManager = LinearLayoutManager(requireContext())
            rvSelectLists.adapter = SelectListAdapter(lists) { selectedList ->
                viewModel.addProductToCustomList(selectedList.id, productId)
                dialog.dismiss()
            }
        }
        dialog.show()
    }

    private fun setupUI(product: ProductDto) {
        Glide.with(this)
            .load(product.getThumbnailUrl())
            .placeholder(android.R.drawable.ic_menu_gallery)
            .into(binding.imgProductDetail)

        binding.tvDetailBrand.text = product.brand ?: ""
        binding.tvDetailName.text = product.name
        binding.tvRating.text = "${product.averageRating} (${product.reviewCount})"
        binding.ratingBarSummary.rating = product.averageRating.toFloat()
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

        binding.tvDescription.text = if (!product.description.isNullOrEmpty()) product.description else "Detaylar yükleniyor..."

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

        binding.rvReviews.layoutManager = LinearLayoutManager(context)
        binding.rvReviews.adapter = reviewAdapter
    }
}