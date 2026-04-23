package com.example.finances.category

import com.example.finances.common.ExpenseGroup
import com.example.finances.common.Flow
import jakarta.validation.constraints.DecimalMax
import jakarta.validation.constraints.DecimalMin
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Size
import java.math.BigDecimal

data class CategoryCreateDTO(
    @field:NotBlank
    @field:Size(min = 2, max = 120)
    val name: String,

    @field:NotNull
    val flow: Flow,

    val expenseGroup: ExpenseGroup? = null,

    @field:DecimalMin("-999999999999.99")
    @field:DecimalMax("999999999999.99")
    val openingBalanceAmount: BigDecimal? = null,
)
