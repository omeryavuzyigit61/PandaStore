package com.allmoviedatabase.pandastore.data.repository

import com.allmoviedatabase.pandastore.data.remote.ApiService
import com.allmoviedatabase.pandastore.domain.repository.AuthRepository

import com.allmoviedatabase.pandastore.model.login.LoginRequest
import com.allmoviedatabase.pandastore.model.login.LoginResponse
import com.allmoviedatabase.pandastore.model.register.RegisterRequest
import com.allmoviedatabase.pandastore.model.register.RegisterResponse
import com.allmoviedatabase.pandastore.model.register.UserDto
import com.allmoviedatabase.pandastore.util.TokenManager
import io.reactivex.rxjava3.core.Single
import javax.inject.Inject

class AuthRepositoryImpl @Inject constructor(
    private val apiService: ApiService,
    private val tokenManager: TokenManager
) : AuthRepository {

    override fun login(email: String, pass: String): Single<LoginResponse> {
        return apiService.login(LoginRequest(email, pass))
            .doOnSuccess { response ->
                // Login başarılı olursa tokenları otomatik kaydet
                // Böylece ViewModel'in token saklama derdi olmaz.
                tokenManager.saveTokens(response.accessToken, response.refreshToken)
            }
    }

    override fun register(request: RegisterRequest): Single<RegisterResponse> {
        // Register işleminde genellikle token dönmez, sadece kayıt olunur.
        // Kullanıcı sonra login olur. O yüzden burada token kaydetmiyoruz.
        return apiService.register(request)
    }

    override fun getProfile(): Single<UserDto> {
        return apiService.getProfile()
    }

    override fun logout() {
        tokenManager.clearTokens()
    }
}