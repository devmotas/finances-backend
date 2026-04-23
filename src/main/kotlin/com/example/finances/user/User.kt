package com.example.finances.user

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EntityListeners
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.jpa.domain.support.AuditingEntityListener
import java.time.Instant

@Entity
@Table(name = "users")
@EntityListeners(AuditingEntityListener::class)
class User(
    var name: String = "",
    @Column(unique = true, nullable = false)
    var email: String = "",
    @Column(nullable = false)
    var password: String = "",

    @Column(name = "default_recurrence_months", nullable = false)
    var defaultRecurrenceMonths: Int = 12,

    @Column(name = "emergency_fund_target_months", nullable = false)
    var emergencyFundTargetMonths: Int = 6,
) {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null

    @CreatedDate
    @Column(nullable = false, updatable = false)
    var createdAt: Instant? = null

    @LastModifiedDate
    var updatedAt: Instant? = null
}
