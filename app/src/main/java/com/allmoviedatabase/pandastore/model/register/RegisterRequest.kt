package com.allmoviedatabase.pandastore.model.register

data class RegisterRequest(
    val firstName: String,
    val lastName: String,
    val email: String,
    val password: String,
    val birthDate: Long?
)