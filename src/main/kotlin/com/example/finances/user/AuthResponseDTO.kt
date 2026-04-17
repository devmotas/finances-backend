package com.example.finances.user

data class AuthResponseDTO(
    val token: String,
    val user: UserDTO,
)
