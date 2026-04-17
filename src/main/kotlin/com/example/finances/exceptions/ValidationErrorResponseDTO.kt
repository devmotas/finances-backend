package com.example.finances.exceptions

data class ValidationErrorResponseDTO(
    val status: Int,
    val message: String,
    val fields: List<FieldErrorDTO>,
)

data class FieldErrorDTO(
    val field: String,
    val message: String,
)
