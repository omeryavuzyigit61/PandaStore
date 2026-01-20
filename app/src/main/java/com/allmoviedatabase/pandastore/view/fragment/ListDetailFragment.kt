package com.allmoviedatabase.pandastore.view.fragment

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.allmoviedatabase.pandastore.R
import com.allmoviedatabase.pandastore.adapter.ProductAdapter
import com.allmoviedatabase.pandastore.databinding.FragmentListDetailBinding
import com.allmoviedatabase.pandastore.viewmodel.ListDetailViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ListDetailFragment : Fragment(R.layout.fragment_list_detail) {

    private lateinit var binding: FragmentListDetailBinding
    private val viewModel: ListDetailViewModel by viewModels()
    private lateinit var adapter: ProductAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentListDetailBinding.bind(view)

        // Argümanı al
        val listId = arguments?.getInt("listId") ?: 0

        setupUI()
        setupObserver()

        if (listId != 0) {
            viewModel.getListDetail(listId)
        } else {
            Toast.makeText(context, "Liste ID bulunamadı", Toast.LENGTH_SHORT).show()
        }
    }

    private fun setupUI() {
        binding.toolbar.setNavigationOnClickListener { findNavController().popBackStack() }
        binding.rvListProducts.layoutManager = GridLayoutManager(context, 2)

        adapter = ProductAdapter(
            onProductClick = { product ->
                val bundle = bundleOf("product" to product)
                findNavController().navigate(R.id.productDetailFragment, bundle)
            },
            onAddToCartClick = {
                Toast.makeText(context, "Sepete eklendi", Toast.LENGTH_SHORT).show()
            },
            onFavoriteClick = {} // Liste detayında favori butonu işlevsiz olabilir veya eklenebilir
        )
        binding.rvListProducts.adapter = adapter
    }

    private fun setupObserver() {
        viewModel.listDetail.observe(viewLifecycleOwner) { list ->
            binding.tvListTitle.text = list.name

            // --- KRİTİK NOKTA ---
            // JSON yapısı: List -> items -> product
            // Bizim Adapter ise direkt List<Product> bekliyor.
            // Bu yüzden item'ların içindeki product'ları alıp yeni bir liste yapıyoruz.

            val realProducts = list.products?.map { it.product }

            if (!realProducts.isNullOrEmpty()) {
                binding.layoutEmptyList.visibility = View.GONE
                binding.rvListProducts.visibility = View.VISIBLE

                // Artık elimizde gerçek, içi dolu ürünler var
                adapter.submitList(realProducts)
            } else {
                binding.layoutEmptyList.visibility = View.VISIBLE
                binding.rvListProducts.visibility = View.GONE
            }
            binding.progressBar.visibility = View.GONE
        }

        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        }

        viewModel.error.observe(viewLifecycleOwner) { error ->
            if (error != null) Toast.makeText(context, error, Toast.LENGTH_SHORT).show()
        }
    }
}