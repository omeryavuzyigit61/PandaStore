package com.allmoviedatabase.pandastore.repository

import com.allmoviedatabase.pandastore.model.login.LoginResponse
import com.allmoviedatabase.pandastore.model.register.RegisterRequest
import com.allmoviedatabase.pandastore.model.register.RegisterResponse
import com.allmoviedatabase.pandastore.model.register.UserDto
import io.reactivex.rxjava3.core.Single

interface AuthRepository {
    fun login(email: String, pass: String): Single<LoginResponse>

    fun register(request: RegisterRequest): Single<RegisterResponse>

    fun getProfile(): Single<UserDto>
    fun logout()
}