package com.example.finances.category

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.Optional

@Repository
interface CategoryRepository : JpaRepository<Category, Long> {
    fun findAllByUserId(userId: Long): List<Category>
    fun findByIdAndUserId(id: Long, userId: Long): Optional<Category>
    fun existsByUserIdAndNameIgnoreCase(userId: Long, name: String): Boolean
}
