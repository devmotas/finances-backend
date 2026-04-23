package com.example.finances.user

import jakarta.validation.constraints.Max
import jakarta.validation.constraints.Min
import jakarta.validation.constraints.Size

data class UserMePatchDTO(
    @field:Size(max = 120)
    val name: String? = null,

    @field:Min(1)
    @field:Max(120)
    val defaultRecurrenceMonths: Int? = null,

    @field:Min(1)
    @field:Max(120)
    val emergencyFundTargetMonths: Int? = null,
)
