package com.allmoviedatabase.pandastore.view.bottomsheet

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import com.allmoviedatabase.pandastore.databinding.BottomSheetAddressBinding
import com.allmoviedatabase.pandastore.model.address.AddressDto
import com.allmoviedatabase.pandastore.model.address.AddressRequest
import com.allmoviedatabase.pandastore.viewmodel.AddressViewModel
import com.allmoviedatabase.pandastore.viewmodel.AuthState
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AddressBottomSheet : BottomSheetDialogFragment() {

    private lateinit var binding: BottomSheetAddressBinding
    private val viewModel: AddressViewModel by viewModels()

    // Düzenleme yapılacaksa adres buraya gelir
    private var addressToEdit: AddressDto? = null

    // İşlem bitince List Fragment'a haber vermek için callback
    var onOperationSuccess: (() -> Unit)? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = BottomSheetAddressBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Argümanlardan gelen adresi al (Eğer varsa)
        arguments?.let {
            addressToEdit = it.getParcelable("address")
        }

        setupUI()
        observeData()
    }

    private fun setupUI() {
        if (addressToEdit != null) {
            // DÜZENLEME MODU
            binding.tvSheetTitle.text = "Adresi Düzenle"
            binding.btnSave.text = "GÜNCELLE"

            // Alanları doldur
            with(binding) {
                etTitle.setText(addressToEdit!!.title)
                etCity.setText(addressToEdit!!.city)
                etDistrict.setText(addressToEdit!!.district)
                etOpenAddress.setText(addressToEdit!!.openAddress)
                etZipCode.setText(addressToEdit!!.zipCode)
            }
        } else {
            // EKLEME MODU
            binding.tvSheetTitle.text = "Yeni Adres Ekle"
            binding.btnSave.text = "KAYDET"
        }

        binding.btnSave.setOnClickListener {
            saveAddress()
        }
    }

    private fun saveAddress() {
        val title = binding.etTitle.text.toString().trim()
        val city = binding.etCity.text.toString().trim()
        val district = binding.etDistrict.text.toString().trim()
        val openAddress = binding.etOpenAddress.text.toString().trim()
        val zipCode = binding.etZipCode.text.toString().trim()

        if (title.isEmpty() || city.isEmpty() || district.isEmpty() || openAddress.isEmpty()) {
            Toast.makeText(context, "Lütfen gerekli alanları doldurun", Toast.LENGTH_SHORT).show()
            return
        }

        val request = AddressRequest(title, city, district, openAddress, zipCode)

        if (addressToEdit != null) {
            viewModel.updateAddress(addressToEdit!!.id, request)
        } else {
            viewModel.addAddress(request)
        }
    }

    private fun observeData() {
        viewModel.operationState.observe(viewLifecycleOwner) { state ->
            when (state) {
                is AuthState.Loading -> {
                    binding.progressBar.visibility = View.VISIBLE
                    binding.btnSave.visibility = View.INVISIBLE
                }
                is AuthState.Success -> {
                    binding.progressBar.visibility = View.GONE
                    Toast.makeText(context, state.data, Toast.LENGTH_SHORT).show()

                    // Listeyi yenilemesi için ana sayfaya haber ver
                    onOperationSuccess?.invoke()
                    dismiss() // Pencereyi kapat
                }
                is AuthState.Error -> {
                    binding.progressBar.visibility = View.GONE
                    binding.btnSave.visibility = View.VISIBLE
                    Toast.makeText(context, state.message, Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    companion object {
        fun newInstance(address: AddressDto? = null): AddressBottomSheet {
            val fragment = AddressBottomSheet()
            val args = Bundle()
            if (address != null) {
                args.putParcelable("address", address)
            }
            fragment.arguments = args
            return fragment
        }
    }
}