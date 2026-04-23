package com.example.finances.transaction

import com.example.finances.category.CategoryRepository
import com.example.finances.common.Flow
import com.example.finances.exceptions.BadRequestException
import com.example.finances.exceptions.CategoryNotFoundException
import com.example.finances.exceptions.TransactionNotFoundException
import com.example.finances.exceptions.UserIdDoNotExistsException
import com.example.finances.repositories.UserRepository
import org.springframework.stereotype.Service
import java.math.BigDecimal
import java.time.LocalDate
import java.time.YearMonth

@Service
class TransactionService(
    private val transactionRepository: TransactionRepository,
    private val categoryRepository: CategoryRepository,
    private val userRepository: UserRepository,
) {
    fun listByMonth(userId: Long, year: Int, month: Int): List<TransactionDTO> {
        val period = YearMonth.of(year, month)
        return transactionRepository
            .findAllByUserIdAndDateBetween(userId, period.atDay(1), period.atEndOfMonth())
            .map { it.toDto() }
    }

    fun create(userId: Long, dto: TransactionCreateDTO): TransactionDTO {
        val user = userRepository.findById(userId)
            .orElseThrow { UserIdDoNotExistsException("Usuário não encontrado.") }
        val category = categoryRepository.findByIdAndUserId(dto.categoryId, userId)
            .orElseThrow { CategoryNotFoundException("Categoria não encontrada.") }
        validateTransactionAmount(dto.amount, category.flow)
        val transaction = transactionRepository.save(
            Transaction(
                user = user,
                category = category,
                description = dto.description,
                amount = dto.amount,
                date = dto.date,
                schedule = dto.schedule,
                flow = category.flow,
            )
        )
        return transaction.toDto()
    }

    fun update(
        userId: Long,
        transactionId: Long,
        dto: TransactionCreateDTO,
        applyToFutureSeries: Boolean,
    ): TransactionDTO {
        val transaction = findOrThrow(userId, transactionId)
        val category = categoryRepository.findByIdAndUserId(dto.categoryId, userId)
            .orElseThrow { CategoryNotFoundException("Categoria não encontrada.") }
        validateTransactionAmount(dto.amount, category.flow)

        val recurrence = transaction.recurrence
        if (applyToFutureSeries && recurrence != null) {
            val from = YearMonth.from(transaction.date).atDay(1)
            val txs = transactionRepository.findAllByRecurrence_IdAndUser_IdAndDateGreaterThanEqual(
                recurrence.id!!,
                userId,
                from,
            )
            for (t in txs) {
                t.category = category
                t.description = dto.description
                t.amount = dto.amount
                t.schedule = dto.schedule
                t.flow = category.flow
                if (t.id == transaction.id) {
                    t.date = dto.date
                }
            }
            transactionRepository.saveAll(txs)
            return findOrThrow(userId, transactionId).toDto()
        }

        transaction.category = category
        transaction.description = dto.description
        transaction.amount = dto.amount
        transaction.date = dto.date
        transaction.schedule = dto.schedule
        transaction.flow = category.flow
        return transactionRepository.save(transaction).toDto()
    }

    fun delete(userId: Long, transactionId: Long, applyToFutureSeries: Boolean) {
        val transaction = findOrThrow(userId, transactionId)
        val recurrence = transaction.recurrence
        if (applyToFutureSeries && recurrence != null) {
            val from = YearMonth.from(transaction.date).atDay(1)
            val txs = transactionRepository.findAllByRecurrence_IdAndUser_IdAndDateGreaterThanEqual(
                recurrence.id!!,
                userId,
                from,
            )
            transactionRepository.deleteAll(txs)
        } else {
            transactionRepository.delete(transaction)
        }
    }

    private fun validateTransactionAmount(amount: BigDecimal, flow: Flow) {
        if (amount.signum() == 0) {
            throw BadRequestException("Informe um valor diferente de zero.")
        }
        if (flow != Flow.investment && amount.signum() < 0) {
            throw BadRequestException("Apenas lançamentos de investimento podem ter valor negativo (resgate).")
        }
    }

    private fun findOrThrow(userId: Long, transactionId: Long): Transaction =
        transactionRepository.findByIdAndUserId(transactionId, userId)
            .orElseThrow { TransactionNotFoundException("Transação não encontrada.") }

    private fun Transaction.toDto() = TransactionDTO(
        id = id!!,
        categoryId = category.id!!,
        description = description,
        amount = amount,
        date = date,
        schedule = schedule.name,
        flow = flow.name,
        recurrenceId = recurrence?.id,
        recurrenceIndex = recurrenceIndex,
    )
}
