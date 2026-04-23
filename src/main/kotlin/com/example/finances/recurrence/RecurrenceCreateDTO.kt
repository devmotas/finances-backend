package com.example.finances.recurrence

import jakarta.validation.constraints.Max
import jakarta.validation.constraints.Min
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Positive
import jakarta.validation.constraints.Size
import java.math.BigDecimal
import java.time.LocalDate

data class RecurrenceCreateDTO(
    @field:NotNull
    val categoryId: Long,

    @field:Size(max = 500)
    val description: String? = null,

    @field:NotNull
    @field:Positive
    val amount: BigDecimal,

    @field:NotNull
    val startDate: LocalDate,

    @field:NotNull
    @field:Min(1)
    @field:Max(120)
    val months: Int,

    @field:Min(1)
    @field:Max(120)
    val installmentTotal: Int? = null,
)
