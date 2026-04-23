package com.example.finances.recurrence

import com.example.finances.category.CategoryRepository
import com.example.finances.common.Flow
import com.example.finances.common.Schedule
import com.example.finances.exceptions.BadRequestException
import com.example.finances.exceptions.CategoryNotFoundException
import com.example.finances.exceptions.RecurrenceNotFoundException
import com.example.finances.exceptions.UserIdDoNotExistsException
import com.example.finances.repositories.UserRepository
import com.example.finances.transaction.Transaction
import com.example.finances.transaction.TransactionRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDate
import java.time.YearMonth

@Service
class RecurrenceService(
    private val recurrenceRepository: RecurrenceRepository,
    private val transactionRepository: TransactionRepository,
    private val categoryRepository: CategoryRepository,
    private val userRepository: UserRepository,
) {
    @Transactional
    fun create(userId: Long, dto: RecurrenceCreateDTO): RecurrenceCreatedDTO {
        val user = userRepository.findById(userId)
            .orElseThrow { UserIdDoNotExistsException("Usuário não encontrado.") }
        val category = categoryRepository.findByIdAndUserId(dto.categoryId, userId)
            .orElseThrow { CategoryNotFoundException("Categoria não encontrada.") }

        if (category.flow == Flow.investment) {
            throw BadRequestException(
                "Não é possível criar recorrência para categorias de investimento. Use a tela de investimentos e registre cada mês (aportes e resgates são pontuais).",
            )
        }

        val installmentTotal = dto.installmentTotal
        if (installmentTotal != null && installmentTotal != dto.months) {
            throw BadRequestException("Para parcelamento, o número de meses deve ser igual ao total de parcelas.")
        }

        val recurrence = recurrenceRepository.save(
            Recurrence(
                user = user,
                category = category,
                description = dto.description,
                amount = dto.amount,
                startDate = dto.startDate,
                months = dto.months,
                installmentTotal = installmentTotal,
            )
        )

        val txs = ArrayList<Transaction>(dto.months)
        for (i in 0 until dto.months) {
            val occurrenceDate = dto.startDate.plusMonths(i.toLong())
            val baseDesc = dto.description?.trim().orEmpty()
            val txDescription =
                if (installmentTotal != null) {
                    val y = installmentTotal
                    val x = i + 1
                    if (baseDesc.isEmpty()) {
                        "Parcela $x de $y"
                    } else {
                        "Parcela $x de $y: $baseDesc"
                    }
                } else {
                    dto.description?.trim()?.ifEmpty { null }
                }
            txs.add(
                Transaction(
                    user = user,
                    category = category,
                    description = txDescription,
                    amount = dto.amount,
                    date = occurrenceDate,
                    schedule = Schedule.fixed,
                    flow = category.flow,
                    recurrence = recurrence,
                    recurrenceIndex = i + 1,
                )
            )
        }
        transactionRepository.saveAll(txs)
        return RecurrenceCreatedDTO(
            recurrenceId = recurrence.id!!,
            createdCount = txs.size,
        )
    }

    @Transactional
    fun update(userId: Long, recurrenceId: Long, dto: RecurrenceUpdateDTO) {
        val recurrence = recurrenceRepository.findByIdAndUserId(recurrenceId, userId)
            .orElseThrow { RecurrenceNotFoundException("Recorrência não encontrada.") }
        val category = categoryRepository.findByIdAndUserId(dto.categoryId, userId)
            .orElseThrow { CategoryNotFoundException("Categoria não encontrada.") }

        val cutoff = YearMonth.now().atDay(1)
        val txs = transactionRepository.findAllByRecurrence_IdAndUser_IdAndDateGreaterThanEqual(
            recurrenceId,
            userId,
            cutoff,
        )
        recurrence.category = category
        recurrence.description = dto.description
        recurrence.amount = dto.amount
        recurrenceRepository.save(recurrence)

        val installmentTotal = recurrence.installmentTotal
        for (t in txs) {
            t.category = category
            t.description =
                if (installmentTotal != null) {
                    val idx = t.recurrenceIndex ?: 1
                    val y = installmentTotal
                    val x = idx
                    val baseDesc = dto.description?.trim().orEmpty()
                    if (baseDesc.isEmpty()) {
                        "Parcela $x de $y"
                    } else {
                        "Parcela $x de $y: $baseDesc"
                    }
                } else {
                    dto.description?.trim()?.ifEmpty { null }
                }
            t.amount = dto.amount
            t.flow = category.flow
        }
        transactionRepository.saveAll(txs)
    }

    @Transactional
    fun deleteFuture(userId: Long, recurrenceId: Long) {
        if (recurrenceRepository.findByIdAndUserId(recurrenceId, userId).isEmpty) {
            throw RecurrenceNotFoundException("Recorrência não encontrada.")
        }
        val cutoff = YearMonth.now().atDay(1)
        val txs = transactionRepository.findAllByRecurrence_IdAndUser_IdAndDateGreaterThanEqual(
            recurrenceId,
            userId,
            cutoff,
        )
        transactionRepository.deleteAll(txs)
    }
}
