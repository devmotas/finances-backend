package com.example.finances.user

import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank

data class LoginRequestDTO(
    @field:NotBlank
    @field:Email
    val email: String,
    @field:NotBlank
    val password: String,
)
