package com.example.finances.transaction

import com.example.finances.common.Flow
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository
import java.math.BigDecimal
import java.time.LocalDate
import java.util.Optional

@Repository
interface TransactionRepository : JpaRepository<Transaction, Long> {
    fun findAllByUserIdAndDateBetween(
        userId: Long,
        from: LocalDate,
        to: LocalDate,
    ): List<Transaction>

    @Query(
        "SELECT t FROM Transaction t JOIN FETCH t.category " +
            "WHERE t.user.id = :userId AND t.flow = :flow AND t.date >= :from AND t.date <= :to " +
            "ORDER BY t.date ASC, t.id ASC",
    )
    fun findAllByUserIdAndFlowAndDateBetween(
        @Param("userId") userId: Long,
        @Param("flow") flow: Flow,
        @Param("from") from: LocalDate,
        @Param("to") to: LocalDate,
    ): List<Transaction>

    @Query(
        "SELECT COALESCE(SUM(t.amount), 0) FROM Transaction t " +
            "WHERE t.user.id = :userId AND t.flow = :flow AND t.date < :before",
    )
    fun sumAmountByUserIdAndFlowAndDateBefore(
        @Param("userId") userId: Long,
        @Param("flow") flow: Flow,
        @Param("before") before: LocalDate,
    ): BigDecimal

    @Query(
        "SELECT COALESCE(SUM(t.amount), 0) FROM Transaction t " +
            "WHERE t.user.id = :userId AND t.flow = :flow AND t.date <= :endDate",
    )
    fun sumAmountByUserIdAndFlowAndDateLessThanOrEqual(
        @Param("userId") userId: Long,
        @Param("flow") flow: Flow,
        @Param("endDate") endDate: LocalDate,
    ): BigDecimal

    @Query(
        "SELECT COALESCE(SUM(t.amount), 0) FROM Transaction t " +
            "WHERE t.user.id = :userId AND t.flow = :flow AND t.date >= :from AND t.date <= :to",
    )
    fun sumAmountByUserIdAndFlowAndDateBetween(
        @Param("userId") userId: Long,
        @Param("flow") flow: Flow,
        @Param("from") from: LocalDate,
        @Param("to") to: LocalDate,
    ): BigDecimal

    @Query(
        "SELECT COALESCE(SUM(t.amount), 0) FROM Transaction t " +
            "WHERE t.user.id = :userId AND t.flow = :flow AND t.category.id = :categoryId AND t.date <= :endDate",
    )
    fun sumAmountByUserIdAndFlowAndCategoryIdAndDateLessThanOrEqual(
        @Param("userId") userId: Long,
        @Param("flow") flow: Flow,
        @Param("categoryId") categoryId: Long,
        @Param("endDate") endDate: LocalDate,
    ): BigDecimal

    @Query(
        "SELECT t FROM Transaction t JOIN FETCH t.category WHERE t.user.id = :userId " +
            "AND t.date >= :from AND t.date <= :to ORDER BY t.date ASC, t.id ASC",
    )
    fun findAllForExport(
        @Param("userId") userId: Long,
        @Param("from") from: LocalDate,
        @Param("to") to: LocalDate,
    ): List<Transaction>

    fun findByIdAndUserId(id: Long, userId: Long): Optional<Transaction>
    fun existsByCategoryId(categoryId: Long): Boolean

    fun findAllByRecurrence_IdAndUser_IdAndDateGreaterThanEqual(
        recurrenceId: Long,
        userId: Long,
        from: LocalDate,
    ): List<Transaction>
}
