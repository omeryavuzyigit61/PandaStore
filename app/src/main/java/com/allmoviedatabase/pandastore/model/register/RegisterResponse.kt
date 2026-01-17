package com.allmoviedatabase.pandastore.model.register

data class RegisterResponse(
    val message: String,
    val user: UserDto?
)