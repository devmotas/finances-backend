package com.example.finances.investment

import java.math.BigDecimal

data class InvestmentMonthPointDTO(
    val year: Int,
    val month: Int,
    val invested: BigDecimal,
    val cumulativeWealth: BigDecimal,
    val displayLabel: String? = null,
    val categoryAmounts: List<BigDecimal> = emptyList(),
)
