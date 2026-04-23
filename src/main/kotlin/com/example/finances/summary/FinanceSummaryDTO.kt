package com.example.finances.summary

import java.math.BigDecimal

data class FinanceSummaryDTO(
    val year: Int,
    val month: Int,
    val accumulatedBalance: BigDecimal,
    val openingBalanceAmount: BigDecimal,
    val monthExpenseTotal: BigDecimal,
    val cumulativeInvestmentContributions: BigDecimal,
    val totalEmergencyReserve: BigDecimal,
    val emergencyFundTargetMonths: Int,
    val monthsOfReserveCovered: BigDecimal?,
)
