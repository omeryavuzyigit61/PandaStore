package com.allmoviedatabase.pandastore.view.fragment
import android.content.Context
import android.os.Bundle
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.allmoviedatabase.pandastore.R
import com.allmoviedatabase.pandastore.databinding.FragmentHomeBinding
import com.allmoviedatabase.pandastore.adapter.ProductAdapter
import com.allmoviedatabase.pandastore.view.bottomsheet.FilterBottomSheet
import com.allmoviedatabase.pandastore.viewmodel.AuthState
import com.allmoviedatabase.pandastore.viewmodel.HomeViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HomeFragment : Fragment(R.layout.fragment_home) {

    private lateinit var binding: FragmentHomeBinding
    private val viewModel: HomeViewModel by viewModels()
    private lateinit var productAdapter: ProductAdapter // Bunu yazman gerekecek

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentHomeBinding.bind(view)

        setupRecyclerView()
        setupSearch()
        setupFilterButton()
        observeData()
    }

    private fun setupRecyclerView() {
        productAdapter = ProductAdapter { product ->
            // Ürüne tıklanınca Detay'a git ve ürünü taşı
            val action = HomeFragmentDirections.actionHomeFragmentToProductDetailFragment(product)
            findNavController().navigate(action)
        }
        binding.rvProducts.adapter = productAdapter
    }

    private fun setupSearch() {
        // 1. ARAMA İŞLEMİ (Klavyedeki Büyüteç/Enter tuşuna basınca)
        binding.etSearch.setOnEditorActionListener { v, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                val query = v.text.toString().trim()

                // Boş arama yapılmasın
                if (query.isNotEmpty()) {
                    viewModel.search(query)
                } else {
                    // Boşsa filtreyi temizleyip hepsini getirsin (İsteğe bağlı)
                    viewModel.loadProducts()
                }

                // Klavyeyi kapat
                val imm = context?.getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
                imm?.hideSoftInputFromWindow(v.windowToken, 0)

                // Odağı kaldır (İmleç yanıp sönmesin)
                binding.etSearch.clearFocus()
                true
            } else false
        }
    }

    private fun setupFilterButton() {
        // 2. FİLTRE BUTONU (Artık TextInputLayout'un EndIcon'u oldu)
        binding.tilSearch.setEndIconOnClickListener {
            val filterSheet = FilterBottomSheet()
            filterSheet.onApplyFilters = { min, max, sort ->
                viewModel.applyFilters(min, max, sort)
            }
            filterSheet.show(parentFragmentManager, "FilterSheet")
        }
    }

    private fun observeData() {
        viewModel.productsState.observe(viewLifecycleOwner) { state ->
            when(state) {
                is AuthState.Loading -> binding.progressBar.visibility = View.VISIBLE
                is AuthState.Success -> {
                    binding.progressBar.visibility = View.GONE
                    productAdapter.submitList(state.data)

                    if (state.data.isEmpty()) {
                        // "Sonuç bulunamadı" yazısı gösterilebilir
                    }
                }
                is AuthState.Error -> {
                    binding.progressBar.visibility = View.GONE
                    //Toast.makeText(context, state.message, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}