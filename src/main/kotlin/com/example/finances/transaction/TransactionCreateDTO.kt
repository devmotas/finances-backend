package com.example.finances.transaction

import com.example.finances.common.Schedule
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Size
import java.math.BigDecimal
import java.time.LocalDate

data class TransactionCreateDTO(
    @field:NotNull
    val categoryId: Long,

    @field:Size(max = 500)
    val description: String? = null,

    @field:NotNull
    val amount: BigDecimal,

    @field:NotNull
    val date: LocalDate,

    @field:NotNull
    val schedule: Schedule,
)
