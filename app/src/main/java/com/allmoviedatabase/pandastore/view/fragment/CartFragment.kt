package com.allmoviedatabase.pandastore.view.fragment

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.allmoviedatabase.pandastore.R
import com.allmoviedatabase.pandastore.adapter.CartAdapter
import com.allmoviedatabase.pandastore.databinding.FragmentCartBinding
import com.allmoviedatabase.pandastore.util.toCurrency
import com.allmoviedatabase.pandastore.viewmodel.CartViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CartFragment : Fragment(R.layout.fragment_cart) {

    private lateinit var binding: FragmentCartBinding
    private val viewModel: CartViewModel by viewModels()
    private lateinit var cartAdapter: CartAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentCartBinding.bind(view)

        setupAdapter()
        setupListeners()
        observeViewModel()

        // Sayfa açılınca veriyi çek
        viewModel.loadCart()
    }

    private fun setupAdapter() {
        // Adapter'ı başlatırken lambda fonksiyonlarını veriyoruz
        cartAdapter = CartAdapter(
            onIncreaseClick = { item ->
                viewModel.updateQuantity(item.id, item.quantity + 1)
            },
            onDecreaseClick = { item ->
                viewModel.updateQuantity(item.id, item.quantity - 1)
            },
            onDeleteClick = { item ->
                viewModel.deleteItem(item.id)
            }
        )

        binding.rvCartItems.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = cartAdapter
        }
    }

    private fun setupListeners() {
        binding.btnGoToCheckout.setOnClickListener {
            if (cartAdapter.currentList.isNotEmpty()) {
                // ARTIK ÇALIŞACAK:
                findNavController().navigate(R.id.action_cartFragment_to_checkoutFragment)
            } else {
                Toast.makeText(context, "Sepetin boş uşağum!", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun observeViewModel() {
        // 1. Sepet Verisi
        viewModel.cartState.observe(viewLifecycleOwner) { response ->
            if (response != null && response.cart.items.isNotEmpty()) {
                // Dolu Sepet Modu
                binding.rvCartItems.visibility = View.VISIBLE
                binding.layoutEmptyCart.visibility = View.GONE
                binding.layoutCheckout.visibility = View.VISIBLE

                // Listeyi Adapter'a bas
                cartAdapter.submitList(response.cart.items)

                // Toplam Tutarı Yaz
                binding.tvTotalPrice.text = response.total.toCurrency()
            } else {
                // Boş Sepet Modu
                binding.rvCartItems.visibility = View.GONE
                binding.layoutEmptyCart.visibility = View.VISIBLE
                binding.layoutCheckout.visibility = View.GONE

                cartAdapter.submitList(emptyList())
            }
        }

        // 2. Yükleniyor
        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            binding.progressBarCart.visibility = if (isLoading) View.VISIBLE else View.GONE
            // Yüklenirken butonlara tıklanmasını engelleyebilirsin istersen
        }

        // 3. Hata
        viewModel.error.observe(viewLifecycleOwner) { error ->
            if (!error.isNullOrEmpty()) {
                Toast.makeText(context, error, Toast.LENGTH_SHORT).show()
            }
        }
    }
}