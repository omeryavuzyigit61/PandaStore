package com.allmoviedatabase.pandastore.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.allmoviedatabase.pandastore.domain.repository.AuthRepository
import com.allmoviedatabase.pandastore.model.login.LoginResponse
import com.allmoviedatabase.pandastore.model.register.RegisterRequest
import com.allmoviedatabase.pandastore.model.register.RegisterResponse
import com.allmoviedatabase.pandastore.util.JwtUtils
import com.allmoviedatabase.pandastore.util.TokenManager
import dagger.hilt.android.lifecycle.HiltViewModel
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.schedulers.Schedulers
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val tokenManager: TokenManager
) : ViewModel() {

    val loginState = MutableLiveData<AuthState<LoginResponse>>()
    val registerState = MutableLiveData<AuthState<RegisterResponse>>()

    private val disposable = CompositeDisposable()

    fun login(email: String, pass: String) {
        loginState.value = AuthState.Loading

        disposable.add(
            authRepository.login(email, pass)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    { response ->
                        // --- DÜZELTME BURADA ---

                        // 1. Token'ın içinden ID'yi söküp alıyoruz
                        val userIdFromToken = JwtUtils.getUserIdFromToken(response.accessToken) ?: ""

                        // 2. Tokenları ve bulduğumuz ID'yi kaydediyoruz
                        tokenManager.saveSession(
                            response.accessToken,
                            response.refreshToken,
                            userIdFromToken // <-- Artık ID'yi buradan veriyoruz
                        )

                        loginState.value = AuthState.Success(response)
                    },
                    { error ->
                        loginState.value = AuthState.Error(error.message ?: "Bilinmeyen hata")
                    }
                )
        )
    }

    // ... register, isUserLoggedIn ve onCleared AYNI KALSIN ...
    fun register(req: RegisterRequest) {
        registerState.value = AuthState.Loading
        disposable.add(
            authRepository.register(req)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    { response -> registerState.value = AuthState.Success(response) },
                    { error -> registerState.value = AuthState.Error(error.message ?: "Kayıt Başarısız") }
                )
        )
    }

    fun isUserLoggedIn(): Boolean {
        return !tokenManager.getAccessToken().isNullOrEmpty()
    }

    override fun onCleared() {
        super.onCleared()
        disposable.clear()
    }
}

// ARTIK SADECE BU VAR (LoginState SİLİNDİ)
sealed class AuthState<out T> {
    object Loading : AuthState<Nothing>()
    data class Success<T>(val data: T) : AuthState<T>()
    data class Error(val message: String) : AuthState<Nothing>()
}