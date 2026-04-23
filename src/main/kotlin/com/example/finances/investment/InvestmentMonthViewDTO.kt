package com.example.finances.investment

import com.example.finances.transaction.TransactionDTO

data class InvestmentMonthViewDTO(
    val summary: InvestmentSummaryDTO,
    val transactions: List<TransactionDTO>,
    val monthlySeries: List<InvestmentMonthPointDTO>,
    val stackCategories: List<InvestmentCategoryStackMetaDTO> = emptyList(),
    val categoryTotalsThroughSelectedMonth: List<InvestmentCategoryTotalDTO> = emptyList(),
)
