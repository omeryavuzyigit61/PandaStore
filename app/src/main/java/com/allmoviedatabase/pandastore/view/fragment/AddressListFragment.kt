package com.allmoviedatabase.pandastore.view

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.allmoviedatabase.pandastore.R
import com.allmoviedatabase.pandastore.databinding.FragmentAddressListBinding
import com.allmoviedatabase.pandastore.view.adapter.AddressAdapter
import com.allmoviedatabase.pandastore.view.bottomsheet.AddressBottomSheet
import com.allmoviedatabase.pandastore.viewmodel.AddressViewModel
import com.allmoviedatabase.pandastore.viewmodel.AuthState
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AddressListFragment : Fragment(R.layout.fragment_address_list) {

    private lateinit var binding: FragmentAddressListBinding
    private val viewModel: AddressViewModel by viewModels()
    private lateinit var adapter: AddressAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentAddressListBinding.bind(view)

        setupRecyclerView()
        setupListeners()

        // İlk açılışta verileri çek
        loadData()
        observeData()
    }

    private fun loadData() {
        viewModel.getAddresses()
    }

    private fun setupRecyclerView() {
        adapter = AddressAdapter(
            onEditClick = { address ->
                // DÜZENLEME: BottomSheet'i adres dolu şekilde aç
                openAddressBottomSheet(address)
            },
            onDeleteClick = { address ->
                // SİLME: Direkt ViewModel'e ilet
                viewModel.deleteAddress(address.id)
            }
        )
        binding.rvAddresses.layoutManager = LinearLayoutManager(context)
        binding.rvAddresses.adapter = adapter
    }

    private fun setupListeners() {
        binding.fabAddAddress.setOnClickListener {
            // EKLEME: BottomSheet'i boş aç
            openAddressBottomSheet(null)
        }
    }

    // BottomSheet Açma Fonksiyonu
    private fun openAddressBottomSheet(address: com.allmoviedatabase.pandastore.model.address.AddressDto?) {
        val bottomSheet = AddressBottomSheet.newInstance(address)

        // BottomSheet işlem bitirince (Kaydet/Güncelle) burası çalışır
        bottomSheet.onOperationSuccess = {
            loadData() // Listeyi yenile
        }

        bottomSheet.show(parentFragmentManager, "AddressBottomSheet")
    }

    private fun observeData() {
        // 1. ADRES LİSTESİ DURUMU
        viewModel.addressListState.observe(viewLifecycleOwner) { state ->
            when(state) {
                is AuthState.Loading -> binding.progressBar.visibility = View.VISIBLE
                is AuthState.Success -> {
                    binding.progressBar.visibility = View.GONE
                    adapter.submitList(state.data)

                    // Liste boşsa kullanıcıya bir şeyler söyleyebilirsin (Opsiyonel)
                    if (state.data.isEmpty()) {
                        Toast.makeText(context, "Henüz kayıtlı adresin yok.", Toast.LENGTH_SHORT).show()
                    }
                }
                is AuthState.Error -> {
                    binding.progressBar.visibility = View.GONE
                    Toast.makeText(context, state.message, Toast.LENGTH_SHORT).show()
                }
            }
        }

        // 2. SİLME İŞLEMİ DURUMU (Ekleme/Güncelleme BottomSheet'te halloluyor)
        viewModel.operationState.observe(viewLifecycleOwner) { state ->
            when(state) {
                is AuthState.Loading -> binding.progressBar.visibility = View.VISIBLE
                is AuthState.Success -> {
                    binding.progressBar.visibility = View.GONE
                    Toast.makeText(context, state.data, Toast.LENGTH_SHORT).show()
                    // Silme başarılı olunca liste ViewModel içinde zaten yenileniyor (deleteAddress fonksiyonunda)
                }
                is AuthState.Error -> {
                    binding.progressBar.visibility = View.GONE
                    Toast.makeText(context, state.message, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}