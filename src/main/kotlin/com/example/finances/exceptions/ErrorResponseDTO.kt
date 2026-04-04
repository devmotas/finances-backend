package com.example.finances.exceptions

@JvmRecord
data class ErrorResponseDTO(val status: Int, val message: String?) 