package com.example.finances.summary

import com.example.finances.category.CategoryRepository
import com.example.finances.common.Flow
import com.example.finances.exceptions.UserIdDoNotExistsException
import com.example.finances.repositories.UserRepository
import com.example.finances.transaction.TransactionRepository
import org.springframework.stereotype.Service
import java.math.BigDecimal
import java.math.RoundingMode
import java.time.YearMonth

@Service
class FinanceSummaryService(
    private val transactionRepository: TransactionRepository,
    private val userRepository: UserRepository,
    private val categoryRepository: CategoryRepository,
) {
    fun summary(userId: Long, year: Int, month: Int): FinanceSummaryDTO {
        val ym = YearMonth.of(year, month)
        val end = ym.atEndOfMonth()
        val monthStart = ym.atDay(1)

        val user = userRepository.findById(userId)
            .orElseThrow { UserIdDoNotExistsException("Usuário não encontrado.") }
        val opening = categoryRepository
            .sumOpeningBalanceAmountByUserIdAndFlow(userId, Flow.investment)
            .setScale(2, RoundingMode.HALF_UP)

        val incomeTotal = transactionRepository.sumAmountByUserIdAndFlowAndDateLessThanOrEqual(
            userId,
            Flow.income,
            end,
        )
        val expenseTotal = transactionRepository.sumAmountByUserIdAndFlowAndDateLessThanOrEqual(
            userId,
            Flow.expense,
            end,
        )
        val investmentTotal = transactionRepository.sumAmountByUserIdAndFlowAndDateLessThanOrEqual(
            userId,
            Flow.investment,
            end,
        )

        val accumulatedBalance =
            incomeTotal.subtract(expenseTotal).subtract(investmentTotal).add(opening)

        val monthExpenseTotal = transactionRepository
            .sumAmountByUserIdAndFlowAndDateBetween(userId, Flow.expense, monthStart, end)
            .setScale(2, RoundingMode.HALF_UP)

        val totalEmergencyReserve = accumulatedBalance.add(investmentTotal)

        val targetMonths = user.emergencyFundTargetMonths

        val monthsCovered =
            if (monthExpenseTotal.compareTo(BigDecimal.ZERO) > 0) {
                totalEmergencyReserve.divide(monthExpenseTotal, 2, RoundingMode.HALF_UP)
            } else {
                null
            }

        return FinanceSummaryDTO(
            year = year,
            month = month,
            accumulatedBalance = accumulatedBalance,
            openingBalanceAmount = opening,
            monthExpenseTotal = monthExpenseTotal,
            cumulativeInvestmentContributions = investmentTotal,
            totalEmergencyReserve = totalEmergencyReserve,
            emergencyFundTargetMonths = targetMonths,
            monthsOfReserveCovered = monthsCovered,
        )
    }
}
