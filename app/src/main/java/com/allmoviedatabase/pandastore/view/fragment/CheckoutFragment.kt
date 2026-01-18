package com.allmoviedatabase.pandastore.view.fragment

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.allmoviedatabase.pandastore.R
import com.allmoviedatabase.pandastore.adapter.SavedAddressAdapter
import com.allmoviedatabase.pandastore.databinding.FragmentCheckoutBinding
import com.allmoviedatabase.pandastore.viewmodel.CheckoutViewModel
import com.allmoviedatabase.pandastore.viewmodel.OrderState
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CheckoutFragment : Fragment(R.layout.fragment_checkout) {

    private lateinit var binding: FragmentCheckoutBinding
    private val viewModel: CheckoutViewModel by viewModels()
    private lateinit var addressAdapter: SavedAddressAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentCheckoutBinding.bind(view)

        setupAddressList()
        setupListeners()
        observeViewModel()
    }

    private fun setupAddressList() {
        // Adapter'a tıklama özelliği veriyoruz
        addressAdapter = SavedAddressAdapter { address ->
            // --- TIKLANINCA FORMU DOLDUR ---

            // Eğer AddressDto içinde 'name' ve 'phone' yoksa, buraları ellemedik.
            // Ama varsa bunları da doldur: binding.etName.setText(address.name) vb.

            binding.etCity.setText(address.city)
            binding.etDistrict.setText(address.district)
            binding.etAddress.setText(address.openAddress)
            binding.etZipCode.setText(address.zipCode ?: "")

            Toast.makeText(context, "${address.title} bilgileri seçildi.", Toast.LENGTH_SHORT).show()
        }

        binding.rvSavedAddresses.apply {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
            adapter = addressAdapter
        }
    }

    private fun setupListeners() {
        binding.btnCompleteOrder.setOnClickListener {
            val name = binding.etName.text.toString().trim()
            val phone = binding.etPhone.text.toString().trim()
            val city = binding.etCity.text.toString().trim()
            val district = binding.etDistrict.text.toString().trim()
            val address = binding.etAddress.text.toString().trim()
            val zipCode = binding.etZipCode.text.toString().trim()
            val note = binding.etNote.text.toString().trim()

            viewModel.submitOrder(name, phone, city, district, address, zipCode, note)
        }
    }

    private fun observeViewModel() {
        // 1. KAYITLI ADRESLERİ DİNLE
        viewModel.savedAddresses.observe(viewLifecycleOwner) { addressList ->
            if (!addressList.isNullOrEmpty()) {
                binding.tvSavedAddressesLabel.visibility = View.VISIBLE
                binding.rvSavedAddresses.visibility = View.VISIBLE
                addressAdapter.submitList(addressList)
            } else {
                // Adres yoksa listeyi gizle
                binding.tvSavedAddressesLabel.visibility = View.GONE
                binding.rvSavedAddresses.visibility = View.GONE
            }
        }

        // 2. SİPARİŞ DURUMUNU DİNLE
        viewModel.orderState.observe(viewLifecycleOwner) { state ->
            when(state) {
                is OrderState.Loading -> {
                    binding.loadingLayout.visibility = View.VISIBLE
                    binding.btnCompleteOrder.isEnabled = false
                }
                is OrderState.Success -> {
                    binding.loadingLayout.visibility = View.GONE
                    showSuccessDialog(state.orderResponse.orderNumber)
                }
                is OrderState.ValidationError -> {
                    binding.loadingLayout.visibility = View.GONE
                    binding.btnCompleteOrder.isEnabled = true
                    Toast.makeText(context, state.message, Toast.LENGTH_SHORT).show()
                }
                is OrderState.Error -> {
                    binding.loadingLayout.visibility = View.GONE
                    binding.btnCompleteOrder.isEnabled = true
                    Toast.makeText(context, state.message, Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    private fun showSuccessDialog(orderNumber: String) {
        AlertDialog.Builder(requireContext())
            .setTitle("Siparişin Alındı! 🚀")
            .setMessage("Tebrikler! Siparişin başarıyla oluşturuldu.\n\nSipariş No: $orderNumber")
            .setCancelable(false) // Boşluğa basınca kapanmasın
            .setPositiveButton("Ana Sayfaya Dön") { _, _ ->
                // Sepet ve Checkout'u back stack'ten silip Ana Sayfaya at
                findNavController().navigate(R.id.action_checkoutFragment_to_homeFragment)
            }
            .show()
    }
}