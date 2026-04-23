package com.example.finances.category

import java.math.BigDecimal

data class CategoryDTO(
    val id: Long,
    val name: String,
    val flow: String,
    val expenseGroup: String?,
    val openingBalanceAmount: BigDecimal,
)
