package com.allmoviedatabase.pandastore.model.register

data class UserDto(
    val id: String,
    val firstName: String,
    val lastName: String,
    val email: String,
    val role: String,
    val birthDate: Long?
)