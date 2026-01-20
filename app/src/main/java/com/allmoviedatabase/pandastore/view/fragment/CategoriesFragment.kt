package com.allmoviedatabase.pandastore.view.fragment

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager // IMPORT EKLENDİ
import com.allmoviedatabase.pandastore.R
import com.allmoviedatabase.pandastore.adapter.MyListsAdapter
import com.allmoviedatabase.pandastore.adapter.ProductAdapter
import com.allmoviedatabase.pandastore.databinding.FragmentCategoriesBinding
import com.allmoviedatabase.pandastore.model.lists.CustomListDto
import com.allmoviedatabase.pandastore.viewmodel.MultiState
import com.allmoviedatabase.pandastore.viewmodel.MyListsViewModel
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.switchmaterial.SwitchMaterial
import com.google.android.material.tabs.TabLayout
import com.google.android.material.textfield.TextInputEditText
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CategoriesFragment : Fragment(R.layout.fragment_categories) {

    private lateinit var binding: FragmentCategoriesBinding
    private val viewModel: MyListsViewModel by viewModels()

    private lateinit var listAdapter: MyListsAdapter
    private lateinit var productAdapter: ProductAdapter

    // 0: Beğendiklerim, 1: Listelerim, 2: Keşfet
    private var currentTab = 0

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentCategoriesBinding.bind(view)

        setupAdapters()
        setupTabs()
        setupListeners()
        observeViewModel()
    }

    private fun setupAdapters() {
        // Varsayılan olarak başta LinearLayoutManager veriyoruz,
        // ama handleTabChange bunu değiştirecek.
        binding.rvLists.layoutManager = LinearLayoutManager(context)

        // 1. LİSTE ADAPTER'I (Listelerim ve Keşfet için)
        listAdapter = MyListsAdapter(
            onListClick = { list ->
                val bundle = bundleOf("listId" to list.id)
                try {
                    findNavController().navigate(R.id.listDetailFragment, bundle)
                } catch (e: Exception) {
                    Toast.makeText(context, "Liste detayı sayfası bulunamadı", Toast.LENGTH_SHORT).show()
                }
            },
            onDeleteClick = { list ->
                if (currentTab == 1) {
                    showOptionsBottomSheet(list)
                } else {
                    Toast.makeText(requireContext(), "Bu liste size ait değil.", Toast.LENGTH_SHORT).show()
                }
            }
        )

        // 2. ÜRÜN ADAPTER'I (Beğendiklerim için)
        productAdapter = ProductAdapter(
            onProductClick = { product ->
                val bundle = bundleOf("product" to product)
                findNavController().navigate(R.id.productDetailFragment, bundle)
            },
            onAddToCartClick = { product ->
                viewModel.addToCart(product.id)
            },
            onFavoriteClick = { product ->
                viewModel.removeFromFavorites(product.id)
            }
        )
    }

    private fun setupTabs() {
        binding.tabLayout.removeAllTabs()
        binding.tabLayout.addTab(binding.tabLayout.newTab().setText("Beğendiklerim"))
        binding.tabLayout.addTab(binding.tabLayout.newTab().setText("Listelerim"))
        binding.tabLayout.addTab(binding.tabLayout.newTab().setText("Keşfet"))

        binding.tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                currentTab = tab?.position ?: 0
                handleTabChange(currentTab)
            }
            override fun onTabUnselected(tab: TabLayout.Tab?) {}
            override fun onTabReselected(tab: TabLayout.Tab?) {}
        })

        // Başlangıçta ilk tabı yükle
        handleTabChange(0)
    }

    private fun handleTabChange(tabIndex: Int) {
        // Listeyi temizle ve loading göster
        binding.rvLists.adapter = null
        binding.progressBar.visibility = View.VISIBLE
        binding.rvLists.visibility = View.GONE
        binding.layoutEmpty.visibility = View.GONE

        // --- İŞTE BURASI DEĞİŞTİ: LAYOUT MANAGER AYARI ---
        if (tabIndex == 0) {
            // TAB 0 (Beğendiklerim): Ürünler olduğu için 2 Sütunlu Grid
            binding.rvLists.layoutManager = GridLayoutManager(context, 2)
        } else {
            // TAB 1 ve 2 (Listeler): Liste olduğu için 1 Sütunlu Linear
            binding.rvLists.layoutManager = LinearLayoutManager(context)
        }

        when (tabIndex) {
            0 -> { // --- BEĞENDİKLERİM ---
                binding.fabAddList.hide()
                binding.tvHeader.text = "Favori Ürünler"
                viewModel.getFavorites()
            }
            1 -> { // --- LİSTELERİM ---
                binding.fabAddList.show()
                binding.tvHeader.text = "Kendi Listelerim"
                viewModel.getMyLists()
            }
            2 -> { // --- KEŞFET ---
                binding.fabAddList.hide()
                binding.tvHeader.text = "Topluluk Listeleri"
                viewModel.getDiscoverLists()
            }
        }
    }

    private fun setupListeners() {
        binding.fabAddList.setOnClickListener {
            showListBottomSheet(null)
        }
    }

    private fun observeViewModel() {
        viewModel.uiState.observe(viewLifecycleOwner) { state ->
            when (state) {
                is MultiState.Loading -> {
                    binding.progressBar.visibility = View.VISIBLE
                    binding.layoutEmpty.visibility = View.GONE
                    binding.rvLists.visibility = View.GONE
                }
                is MultiState.SuccessLists -> {
                    // LİSTELER GELDİ -> ListAdapter kullan
                    binding.progressBar.visibility = View.GONE
                    binding.rvLists.visibility = View.VISIBLE
                    binding.layoutEmpty.visibility = View.GONE

                    binding.rvLists.adapter = listAdapter
                    listAdapter.submitList(state.data)
                }
                is MultiState.SuccessProducts -> {
                    // ÜRÜNLER GELDİ -> ProductAdapter kullan
                    binding.progressBar.visibility = View.GONE
                    binding.rvLists.visibility = View.VISIBLE
                    binding.layoutEmpty.visibility = View.GONE

                    binding.rvLists.adapter = productAdapter
                    productAdapter.submitList(state.data)
                }
                is MultiState.Empty -> {
                    binding.progressBar.visibility = View.GONE
                    binding.rvLists.visibility = View.GONE
                    binding.layoutEmpty.visibility = View.VISIBLE

                    val emptyLayout = binding.layoutEmpty
                    if(emptyLayout.childCount > 1) {
                        val textView = emptyLayout.getChildAt(1) as? TextView
                        textView?.text = if(currentTab == 0) "Favori ürünün yok" else "Liste bulunamadı"
                    }
                }
                is MultiState.Error -> {
                    binding.progressBar.visibility = View.GONE
                    Toast.makeText(requireContext(), state.message, Toast.LENGTH_SHORT).show()
                }
            }
        }

        viewModel.actionMessage.observe(viewLifecycleOwner) { msg ->
            if (msg != null) {
                Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
                viewModel.clearActionMessage()
            }
        }
    }

    // --- BOTTOM SHEET FONKSİYONLARI (DEĞİŞİKLİK YOK) ---
    private fun showListBottomSheet(listToEdit: CustomListDto?) {
        val dialog = BottomSheetDialog(requireContext())
        val view = layoutInflater.inflate(R.layout.bottom_sheet_create_list, null)
        dialog.setContentView(view)

        val tvTitle = view.findViewById<TextView>(R.id.tvSheetTitle)
        val etName = view.findViewById<TextInputEditText>(R.id.etListName)
        val switchPrivacy = view.findViewById<SwitchMaterial>(R.id.switchPrivacy)
        val tvPrivacyTitle = view.findViewById<TextView>(R.id.tvPrivacyTitle)
        val tvPrivacyDesc = view.findViewById<TextView>(R.id.tvPrivacyDesc)
        val imgPrivacy = view.findViewById<ImageView>(R.id.imgPrivacy)
        val btnSave = view.findViewById<Button>(R.id.btnSaveList)

        fun updatePrivacyUI(isPrivate: Boolean) {
            if (isPrivate) {
                tvPrivacyTitle.text = "Gizli Liste"
                tvPrivacyDesc.text = "Sadece sen görebilirsin"
                imgPrivacy.setImageResource(R.drawable.ic_lock)
            } else {
                tvPrivacyTitle.text = "Herkese Açık"
                tvPrivacyDesc.text = "Keşfet'te herkes görebilir"
                imgPrivacy.setImageResource(R.drawable.ic_world)
            }
        }

        switchPrivacy.setOnCheckedChangeListener { _, isChecked -> updatePrivacyUI(isChecked) }

        if (listToEdit != null) {
            tvTitle.text = "Listeyi Düzenle"
            btnSave.text = "GÜNCELLE"
            etName.setText(listToEdit.name)
            switchPrivacy.isChecked = listToEdit.isPrivate
            updatePrivacyUI(listToEdit.isPrivate)
        } else {
            tvTitle.text = "Yeni Liste Oluştur"
            btnSave.text = "OLUŞTUR"
            switchPrivacy.isChecked = true
            updatePrivacyUI(true)
        }

        btnSave.setOnClickListener {
            val name = etName.text.toString().trim()
            if (name.isNotEmpty()) {
                if (listToEdit != null) viewModel.updateList(listToEdit.id, name, switchPrivacy.isChecked)
                else viewModel.createList(name, null, "📁", "#FF9800")
                dialog.dismiss()
            } else {
                etName.error = "Liste adı boş olamaz"
            }
        }
        dialog.show()
    }

    private fun showOptionsBottomSheet(list: CustomListDto) {
        val dialog = BottomSheetDialog(requireContext())
        val view = layoutInflater.inflate(R.layout.bottom_sheet_options, null)
        dialog.setContentView(view)
        view.findViewById<View>(R.id.btnEdit).setOnClickListener { dialog.dismiss(); showListBottomSheet(list) }
        view.findViewById<View>(R.id.btnDelete).setOnClickListener { dialog.dismiss(); showDeleteConfirmationDialog(list) }
        dialog.show()
    }

    private fun showDeleteConfirmationDialog(list: CustomListDto) {
        AlertDialog.Builder(requireContext())
            .setTitle("Listeyi Sil")
            .setMessage("${list.name} silinecek.")
            .setPositiveButton("SİL") { _, _ -> viewModel.deleteList(list.id) }
            .setNegativeButton("VAZGEÇ", null)
            .show()
    }
}