package com.example.finances.transaction

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.time.LocalDate
import java.util.Optional

@Repository
interface TransactionRepository : JpaRepository<Transaction, Long> {
    fun findAllByUserIdAndDateBetween(
        userId: Long,
        from: LocalDate,
        to: LocalDate,
    ): List<Transaction>

    fun findByIdAndUserId(id: Long, userId: Long): Optional<Transaction>
    fun existsByCategoryId(categoryId: Long): Boolean
}
