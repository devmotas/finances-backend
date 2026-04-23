package com.example.finances.recurrence

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.Optional

@Repository
interface RecurrenceRepository : JpaRepository<Recurrence, Long> {
    fun findByIdAndUserId(id: Long, userId: Long): Optional<Recurrence>
}
