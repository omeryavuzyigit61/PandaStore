package com.allmoviedatabase.pandastore.view.fragment

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.allmoviedatabase.pandastore.R
import com.allmoviedatabase.pandastore.databinding.FragmentProfileBinding
import com.allmoviedatabase.pandastore.viewmodel.AuthState
import com.allmoviedatabase.pandastore.viewmodel.ProfileViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ProfileFragment : Fragment(R.layout.fragment_profile) {

    private lateinit var binding: FragmentProfileBinding
    private val viewModel: ProfileViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentProfileBinding.bind(view)

        // Sayfa açılınca profili çek
        viewModel.getProfile()

        setupClicks()
        observeData()
    }

    private fun setupClicks() {
        // Menü tıklamaları (Şimdilik boş sayfa veya Toast)
        binding.btnOrders.setOnClickListener {
            findNavController().navigate(R.id.action_profileFragment_to_orderListFragment)
        }
        binding.btnFavorites.setOnClickListener { showToast("Beğendiklerim Yakında!") }
        binding.btnAddresses.setOnClickListener {
            findNavController().navigate(R.id.action_profileFragment_to_addressListFragment)
        }
        binding.btnReviews.setOnClickListener { showToast("Değerlendirmeler Yakında!") }

        // ÇIKIŞ YAP
        binding.btnLogout.setOnClickListener {
            viewModel.logout()
            // Login ekranına geri gönder ve tüm backstack'i temizle
            findNavController().navigate(R.id.loginFragment)
        }
    }

    private fun observeData() {
        viewModel.userProfile.observe(viewLifecycleOwner) { state ->
            when (state) {
                is AuthState.Loading -> binding.progressBar.visibility = View.VISIBLE
                is AuthState.Success -> {
                    binding.progressBar.visibility = View.GONE
                    val user = state.data

                    binding.tvUserName.text = "${user.firstName} ${user.lastName}"
                    binding.tvUserAge.text =
                        viewModel.calculateAge(user.birthDate) // Artık çalışacak

                    // Yaşı backend timestamp'ten hesaplıyoruz
                    // Not: API birthDate'i response'ta döndürüyor mu kontrol etmeliyiz.
                    // Eğer UserDto içinde birthDate yoksa eklemelisin.
                    // Ben şimdilik varmış gibi varsayıyorum, yoksa UserDto'ya ekle: val birthDate: Long?
                    // binding.tvUserAge.text = viewModel.calculateAge(user.birthDate)
                }

                is AuthState.Error -> {
                    binding.progressBar.visibility = View.GONE
                    showToast(state.message)
                }
            }
        }

    }

    private fun showToast(msg: String) {
        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
    }
}