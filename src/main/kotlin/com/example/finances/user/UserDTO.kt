package com.example.finances.user

import java.time.Instant

data class UserDTO(
    val id: Long,
    val name: String,
    val email: String,
    val createdAt: Instant?,
    val updatedAt: Instant?,
    val defaultRecurrenceMonths: Int,
    val emergencyFundTargetMonths: Int,
)
