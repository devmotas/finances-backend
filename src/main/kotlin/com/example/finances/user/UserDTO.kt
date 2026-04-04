package com.example.finances.user

import java.time.Instant

data class UserDTO(
    val id: Int,
    val name: String,
    val email: String,
    val createdAt: Instant?,
    val updatedAt: Instant?,
)
