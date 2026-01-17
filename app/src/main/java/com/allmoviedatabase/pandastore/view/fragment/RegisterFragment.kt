package com.allmoviedatabase.pandastore.view.fragment

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.allmoviedatabase.pandastore.R
import com.allmoviedatabase.pandastore.databinding.FragmentRegisterBinding
import com.allmoviedatabase.pandastore.model.register.RegisterRequest
import com.allmoviedatabase.pandastore.viewmodel.AuthState
import com.allmoviedatabase.pandastore.viewmodel.AuthViewModel
import com.google.android.material.datepicker.MaterialDatePicker
import dagger.hilt.android.AndroidEntryPoint
import java.text.SimpleDateFormat
import java.util.Locale

@AndroidEntryPoint
class RegisterFragment : Fragment(R.layout.fragment_register) {

    private lateinit var binding: FragmentRegisterBinding
    private val viewModel: AuthViewModel by viewModels()
    private var selectedBirthDateTimestamp: Long? = null // Seçilen tarih burada tutulur

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentRegisterBinding.bind(view)

        setupClickListeners()
        observeData()
    }

    private fun setupClickListeners() {
        // Takvim Seçimi
        binding.etBirthDate.setOnClickListener {
            val datePicker = MaterialDatePicker.Builder.datePicker()
                .setTitleText("Doğum Tarihini Seç")
                .setSelection(MaterialDatePicker.todayInUtcMilliseconds())
                .build()

            datePicker.addOnPositiveButtonClickListener { selection ->
                selectedBirthDateTimestamp = selection // API'ye gidecek veri (Long)

                // Ekranda gösterilecek format (14/01/2026)
                val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                binding.etBirthDate.setText(sdf.format(selection))
            }
            datePicker.show(parentFragmentManager, "DATE_PICKER")
        }

        // Kayıt Ol Butonu
        binding.btnRegister.setOnClickListener {
            val name = binding.etName.text.toString().trim()
            val surname = binding.etSurname.text.toString().trim()
            val email = binding.etEmail.text.toString().trim()
            val password = binding.etPassword.text.toString().trim()

            if (name.isEmpty() || surname.isEmpty() || email.isEmpty() || password.isEmpty()) {
                Toast.makeText(context, "Lütfen tüm alanları doldurun", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // ViewModel'e isteği gönderiyoruz
            val request = RegisterRequest(
                firstName = name,
                lastName = surname,
                email = email,
                password = password,
                birthDate = selectedBirthDateTimestamp // null olabilir, sorun değil
            )
            viewModel.register(request)
        }

        // Zaten Üyeyim Butonu -> Geri Dön
        binding.tvGoToLogin.setOnClickListener {
            findNavController().popBackStack() // Geri tuşu gibi çalışır
        }
    }

    private fun observeData() {
        viewModel.registerState.observe(viewLifecycleOwner) { state ->
            when (state) {
                is AuthState.Loading -> {
                    binding.progressBar.visibility = View.VISIBLE
                    binding.btnRegister.isEnabled = false
                }
                is AuthState.Success -> {
                    binding.progressBar.visibility = View.GONE
                    binding.btnRegister.isEnabled = true
                    Toast.makeText(context, "Kayıt Başarılı! Giriş yapabilirsiniz.", Toast.LENGTH_LONG).show()

                    // Başarılı olunca Login ekranına geri gönder
                    findNavController().popBackStack()
                }
                is AuthState.Error -> {
                    binding.progressBar.visibility = View.GONE
                    binding.btnRegister.isEnabled = true
                    Toast.makeText(context, state.message, Toast.LENGTH_LONG).show()
                }
            }
        }
    }
}