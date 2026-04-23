package com.example.finances.transaction

import java.math.BigDecimal
import java.time.LocalDate

data class TransactionDTO(
    val id: Long,
    val categoryId: Long,
    val description: String?,
    val amount: BigDecimal,
    val date: LocalDate,
    val schedule: String,
    val flow: String,
    val recurrenceId: Long?,
    val recurrenceIndex: Int?,
)
