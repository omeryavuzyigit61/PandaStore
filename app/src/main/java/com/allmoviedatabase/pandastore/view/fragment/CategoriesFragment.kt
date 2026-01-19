package com.allmoviedatabase.pandastore.view.fragment

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.allmoviedatabase.pandastore.R
import com.allmoviedatabase.pandastore.adapter.MyListsAdapter
import com.allmoviedatabase.pandastore.databinding.FragmentCategoriesBinding
import com.allmoviedatabase.pandastore.model.lists.CustomListDto
import com.allmoviedatabase.pandastore.viewmodel.MyListsState
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
    private lateinit var adapter: MyListsAdapter

    // Hangi sekmedeyiz? 0: Listelerim, 1: Keşfet
    private var currentTab = 0

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentCategoriesBinding.bind(view)

        // Tab'ları ekle
        binding.tabLayout.addTab(binding.tabLayout.newTab().setText("Kendi Listelerim"))
        binding.tabLayout.addTab(binding.tabLayout.newTab().setText("Keşfet (Diğerleri)"))

        setupRecyclerView()
        setupTabs()
        setupListeners()
        observeViewModel()
    }

    private fun setupRecyclerView() {
        adapter = MyListsAdapter(
            onListClick = { list ->
                // Detaya git
                Toast.makeText(requireContext(), "${list.name} seçildi", Toast.LENGTH_SHORT).show()
            },
            onDeleteClick = { list ->
                if (currentTab == 0) {
                    // ARTIK O ÇİRKİN DİALOG DEĞİL, BOTTOM SHEET AÇILIYOR
                    showOptionsBottomSheet(list)
                } else {
                    Toast.makeText(requireContext(), "Bu liste size ait değil.", Toast.LENGTH_SHORT).show()
                }
            }
        )

        binding.rvLists.layoutManager = LinearLayoutManager(context)
        binding.rvLists.adapter = adapter
    }

    private fun setupTabs() {
        binding.tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                currentTab = tab?.position ?: 0

                if (currentTab == 0) {
                    // --- LİSTELERİM ---
                    binding.fabAddList.show()
                    viewModel.getMyLists()
                } else {
                    // --- KEŞFET ---
                    binding.fabAddList.hide()
                    viewModel.getDiscoverLists()
                }
            }
            override fun onTabUnselected(tab: TabLayout.Tab?) {}
            override fun onTabReselected(tab: TabLayout.Tab?) {}
        })
    }

    private fun setupListeners() {
        binding.fabAddList.setOnClickListener {
            // Yeni oluşturma
            showListBottomSheet(null)
        }
    }

    // --- 1. MODAL: LİSTE OLUŞTURMA / DÜZENLEME ---
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

        switchPrivacy.setOnCheckedChangeListener { _, isChecked ->
            updatePrivacyUI(isChecked)
        }

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
            val isPrivate = switchPrivacy.isChecked

            if (name.isNotEmpty()) {
                if (listToEdit != null) {
                    viewModel.updateList(listToEdit.id, name, isPrivate)
                } else {
                    viewModel.createList(name, null, "📁", "#FF9800")
                }
                dialog.dismiss()
            } else {
                etName.error = "Liste adı boş olamaz"
            }
        }
        dialog.show()
    }

    // --- 2. MODAL: SEÇENEKLER (DÜZENLE / SİL) ---
    // İşte burası o çirkin AlertDialog yerine geçen yeni yapı:
    private fun showOptionsBottomSheet(list: CustomListDto) {
        val dialog = BottomSheetDialog(requireContext())
        // Yeni oluşturduğun layout'u buraya bağlıyoruz
        val view = layoutInflater.inflate(R.layout.bottom_sheet_options, null)
        dialog.setContentView(view)

        // DÜZENLE TIKLANIRSA
        view.findViewById<View>(R.id.btnEdit).setOnClickListener {
            dialog.dismiss() // Önce menüyü kapat
            showListBottomSheet(list) // Sonra düzenleme ekranını aç
        }

        // SİL TIKLANIRSA
        view.findViewById<View>(R.id.btnDelete).setOnClickListener {
            dialog.dismiss() // Önce menüyü kapat
            showDeleteConfirmationDialog(list) // Sonra "Emin misin" sorusunu sor
        }

        dialog.show()
    }

    // Silme onayı için AlertDialog kalabilir (Bu standarttır), ama menü artık havalı oldu.
    private fun showDeleteConfirmationDialog(list: CustomListDto) {
        AlertDialog.Builder(requireContext())
            .setTitle("Listeyi Sil")
            .setMessage("${list.name} silinecek. Geri alınamaz.")
            .setPositiveButton("SİL") { _, _ ->
                viewModel.deleteList(list.id)
            }
            .setNegativeButton("VAZGEÇ", null)
            .show()
    }

    private fun observeViewModel() {
        viewModel.listsState.observe(viewLifecycleOwner) { state ->
            when (state) {
                is MyListsState.Loading -> {
                    binding.progressBar.visibility = View.VISIBLE
                    binding.layoutEmpty.visibility = View.GONE
                    binding.rvLists.visibility = View.GONE
                }
                is MyListsState.Success -> {
                    binding.progressBar.visibility = View.GONE
                    binding.layoutEmpty.visibility = View.GONE
                    binding.rvLists.visibility = View.VISIBLE
                    adapter.submitList(state.data)
                }
                is MyListsState.Empty -> {
                    binding.progressBar.visibility = View.GONE
                    binding.rvLists.visibility = View.GONE
                    if (currentTab == 0) {
                        binding.layoutEmpty.visibility = View.VISIBLE
                    } else {
                        binding.layoutEmpty.visibility = View.VISIBLE
                    }
                    adapter.submitList(emptyList())
                }
                is MyListsState.Error -> {
                    binding.progressBar.visibility = View.GONE
                    Toast.makeText(requireContext(), state.message, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}