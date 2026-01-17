package com.allmoviedatabase.pandastore.view.bottomsheet

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.allmoviedatabase.pandastore.R
import com.allmoviedatabase.pandastore.databinding.BottomSheetFilterBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class FilterBottomSheet : BottomSheetDialogFragment() {

    private lateinit var binding: BottomSheetFilterBinding

    // HomeFragment'a veri göndermek için callback fonksiyonu
    var onApplyFilters: ((min: Double?, max: Double?, sort: String?) -> Unit)? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = BottomSheetFilterBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnApply.setOnClickListener {
            // 1. Hangi sıralama seçildi?
            val sortOption = when(binding.radioGroupSort.checkedRadioButtonId) {
                R.id.rbPriceLow -> "price_low"
                R.id.rbPriceHigh -> "price_high"
                R.id.rbNewest -> "newest"
                R.id.rbPopular -> "popular"
                else -> null
            }

            // 2. Fiyatlar girildi mi? (Boşsa null gönderelim)
            val minString = binding.etMinPrice.text.toString()
            val maxString = binding.etMaxPrice.text.toString()

            val min = if(minString.isNotEmpty()) minString.toDouble() else null
            val max = if(maxString.isNotEmpty()) maxString.toDouble() else null

            // 3. Verileri yolla ve kapat
            onApplyFilters?.invoke(min, max, sortOption)
            dismiss()
        }
    }
}