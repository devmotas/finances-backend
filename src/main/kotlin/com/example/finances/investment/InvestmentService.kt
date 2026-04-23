package com.example.finances.investment

import com.example.finances.category.Category
import com.example.finances.category.CategoryRepository
import com.example.finances.common.Flow
import com.example.finances.transaction.Transaction
import com.example.finances.transaction.TransactionRepository
import com.example.finances.transaction.TransactionService
import org.springframework.stereotype.Service
import java.math.BigDecimal
import java.math.RoundingMode
import java.time.YearMonth

@Service
class InvestmentService(
    private val transactionService: TransactionService,
    private val transactionRepository: TransactionRepository,
    private val categoryRepository: CategoryRepository,
) {
    fun monthView(userId: Long, year: Int, month: Int): InvestmentMonthViewDTO {
        val ym = YearMonth.of(year, month)
        val monthStart = ym.atDay(1)

        val monthTransactions = transactionService
            .listByMonth(userId, year, month)
            .filter { it.flow == Flow.investment.name }

        val monthTotal =
            monthTransactions.fold(BigDecimal.ZERO) { acc, t -> acc.add(t.amount) }

        val openingBalanceTotal = categoryRepository
            .sumOpeningBalanceAmountByUserIdAndFlow(userId, Flow.investment)
            .setScale(2, RoundingMode.HALF_UP)

        val accumulatedBeforeMonth = transactionRepository.sumAmountByUserIdAndFlowAndDateBefore(
            userId,
            Flow.investment,
            monthStart,
        )

        val totalThroughMonthEnd = accumulatedBeforeMonth.add(monthTotal)
        val positionBeforeMonth = openingBalanceTotal.add(accumulatedBeforeMonth)
        val positionThroughMonthEnd = openingBalanceTotal.add(totalThroughMonthEnd)

        val windowStart = ym.minusMonths(11).atDay(1)
        val windowEnd = ym.atEndOfMonth()
        val windowTxs = transactionRepository.findAllByUserIdAndFlowAndDateBetween(
            userId,
            Flow.investment,
            windowStart,
            windowEnd,
        )

        val priorInWindow = transactionRepository.sumAmountByUserIdAndFlowAndDateBefore(
            userId,
            Flow.investment,
            windowStart,
        )

        val investmentCats = investmentCategoriesOrdered(userId)
        val stackCategories = investmentCats.map { cat ->
            InvestmentCategoryStackMetaDTO(id = cat.id!!, name = cat.name)
        }

        val openingVec = openingAmountVector(investmentCats)
        val monthlySeries = mutableListOf<InvestmentMonthPointDTO>()
        monthlySeries.add(
            InvestmentMonthPointDTO(
                year = 0,
                month = 0,
                invested = openingVec.fold(BigDecimal.ZERO) { a, b -> a.add(b) },
                cumulativeWealth = openingBalanceTotal,
                displayLabel = "Saldo inicial",
                categoryAmounts = openingVec,
            ),
        )
        var cursor = YearMonth.from(windowStart)
        var runningInWindow = BigDecimal.ZERO
        while (!cursor.isAfter(ym)) {
            val catVec = monthAmountVector(investmentCats, windowTxs, cursor)
            val invested = catVec.fold(BigDecimal.ZERO) { a, b -> a.add(b) }
            runningInWindow = runningInWindow.add(invested)
            val cumulativeWealth = openingBalanceTotal.add(priorInWindow).add(runningInWindow)
            monthlySeries.add(
                InvestmentMonthPointDTO(
                    year = cursor.year,
                    month = cursor.monthValue,
                    invested = invested,
                    cumulativeWealth = cumulativeWealth,
                    displayLabel = null,
                    categoryAmounts = catVec,
                ),
            )
            cursor = cursor.plusMonths(1)
        }

        val endSelected = ym.atEndOfMonth()
        val categoryTotalsThroughSelectedMonth = investmentCats.map { cat ->
            val cid = cat.id!!
            val contrib = transactionRepository.sumAmountByUserIdAndFlowAndCategoryIdAndDateLessThanOrEqual(
                userId,
                Flow.investment,
                cid,
                endSelected,
            )
            val total = cat.openingBalanceAmount.add(contrib).setScale(2, RoundingMode.HALF_UP)
            InvestmentCategoryTotalDTO(
                categoryId = cid,
                name = cat.name,
                totalThroughMonthEnd = total,
            )
        }

        return InvestmentMonthViewDTO(
            summary = InvestmentSummaryDTO(
                monthTotal = monthTotal,
                accumulatedBeforeMonth = accumulatedBeforeMonth,
                totalThroughMonthEnd = totalThroughMonthEnd,
                openingBalanceTotal = openingBalanceTotal,
                positionBeforeMonth = positionBeforeMonth,
                positionThroughMonthEnd = positionThroughMonthEnd,
            ),
            transactions = monthTransactions,
            monthlySeries = monthlySeries,
            stackCategories = stackCategories,
            categoryTotalsThroughSelectedMonth = categoryTotalsThroughSelectedMonth,
        )
    }

    private fun investmentCategoriesOrdered(userId: Long): List<Category> =
        categoryRepository.findAllByUserId(userId)
            .filter { it.flow == Flow.investment }
            .sortedWith(compareBy({ it.name }, { it.id }))

    private fun openingAmountVector(cats: List<Category>): List<BigDecimal> =
        cats.map { it.openingBalanceAmount.setScale(2, RoundingMode.HALF_UP) }

    private fun monthAmountVector(
        cats: List<Category>,
        windowTxs: List<Transaction>,
        cursor: YearMonth,
    ): List<BigDecimal> {
        val from = cursor.atDay(1)
        val to = cursor.atEndOfMonth()
        return cats.map { cat ->
            val catId = cat.id!!
            windowTxs
                .filter { tx ->
                    !tx.date.isBefore(from) && !tx.date.isAfter(to) && tx.category.id == catId
                }
                .fold(BigDecimal.ZERO) { acc, t -> acc.add(t.amount) }
                .setScale(2, RoundingMode.HALF_UP)
        }
    }
}
