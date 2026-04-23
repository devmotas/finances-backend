package com.example.finances.investment

import java.math.BigDecimal

data class InvestmentCategoryTotalDTO(
    val categoryId: Long,
    val name: String,
    val totalThroughMonthEnd: BigDecimal,
)
