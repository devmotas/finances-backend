package com.example.finances.investment

import java.math.BigDecimal

data class InvestmentSummaryDTO(
    val monthTotal: BigDecimal,
    val accumulatedBeforeMonth: BigDecimal,
    val totalThroughMonthEnd: BigDecimal,
    val openingBalanceTotal: BigDecimal,
    val positionBeforeMonth: BigDecimal,
    val positionThroughMonthEnd: BigDecimal,
)
