package com.allmoviedatabase.pandastore.view.fragment

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.allmoviedatabase.pandastore.R
import com.allmoviedatabase.pandastore.databinding.FragmentLoginBinding
import com.allmoviedatabase.pandastore.viewmodel.AuthState
import com.allmoviedatabase.pandastore.viewmodel.AuthViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LoginFragment : Fragment(R.layout.fragment_login) {

    private lateinit var binding: FragmentLoginBinding
    private val viewModel: AuthViewModel by viewModels()

    // 1. OTOMATİK GİRİŞ KONTROLÜ
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Eğer kullanıcı zaten giriş yapmışsa direkt Ana Sayfaya at
        if (viewModel.isUserLoggedIn()) {
            navigateToHome()
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentLoginBinding.bind(view)

        // Giriş Yap Butonu
        binding.btnLogin.setOnClickListener {
            val email = binding.etEmail.text.toString().trim()
            val pass = binding.etPassword.text.toString().trim()
            if (email.isNotEmpty() && pass.isNotEmpty()) {
                viewModel.login(email, pass)
            } else {
                Toast.makeText(context, "Bilgileri eksik girme uşağum!", Toast.LENGTH_SHORT).show()
            }
        }

        binding.tvGoToRegister.setOnClickListener {
            findNavController().navigate(R.id.action_loginFragment_to_registerFragment)
        }

        observeData()
    }

    private fun observeData() {
        viewModel.loginState.observe(viewLifecycleOwner) { state ->
            // HATA ÇÖZÜMÜ: 'is AuthState.Success' yerine 'is AuthState.Success<*>' kullanıyoruz
            when(state) {
                is AuthState.Loading -> {
                    binding.progressBar.visibility = View.VISIBLE
                    binding.btnLogin.isEnabled = false
                }
                is AuthState.Success<*> -> { // <--- DÜZELTME BURADA (<*>)
                    binding.progressBar.visibility = View.GONE
                    binding.btnLogin.isEnabled = true
                    Toast.makeText(context, "Hoşgeldin!", Toast.LENGTH_SHORT).show()

                    navigateToHome()
                }
                is AuthState.Error -> {
                    binding.progressBar.visibility = View.GONE
                    binding.btnLogin.isEnabled = true
                    Toast.makeText(context, "Hata: ${state.message}", Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    // Ana Sayfaya yönlendirme fonksiyonu (Kod tekrarını önlemek için)
    private fun navigateToHome() {
        // NavGraph'ta bu action'ı tanımlaman lazım (Aşağıda gösterdim)
        findNavController().navigate(R.id.action_loginFragment_to_homeFragment)
    }
}