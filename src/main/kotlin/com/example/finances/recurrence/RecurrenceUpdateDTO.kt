package com.example.finances.recurrence

import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Positive
import jakarta.validation.constraints.Size
import java.math.BigDecimal

data class RecurrenceUpdateDTO(
    @field:NotNull
    val categoryId: Long,

    @field:Size(max = 500)
    val description: String? = null,

    @field:NotNull
    @field:Positive
    val amount: BigDecimal,
)
