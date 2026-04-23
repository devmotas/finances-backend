package com.example.finances.category

import com.example.finances.common.Flow
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository
import java.math.BigDecimal
import java.util.Optional

@Repository
interface CategoryRepository : JpaRepository<Category, Long> {
    fun findAllByUserId(userId: Long): List<Category>
    fun findByIdAndUserId(id: Long, userId: Long): Optional<Category>
    fun existsByUserIdAndNameIgnoreCase(userId: Long, name: String): Boolean

    @Query(
        "SELECT COALESCE(SUM(c.openingBalanceAmount), 0) FROM Category c " +
            "WHERE c.user.id = :userId AND c.flow = :flow",
    )
    fun sumOpeningBalanceAmountByUserIdAndFlow(
        @Param("userId") userId: Long,
        @Param("flow") flow: Flow,
    ): BigDecimal
}
