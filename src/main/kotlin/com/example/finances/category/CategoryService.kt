package com.example.finances.category

import com.example.finances.common.Flow
import com.example.finances.exceptions.CategoryHasTransactionsException
import com.example.finances.exceptions.CategoryNotFoundException
import com.example.finances.exceptions.DuplicateCategoryNameException
import com.example.finances.repositories.UserRepository
import com.example.finances.exceptions.UserIdDoNotExistsException
import com.example.finances.transaction.TransactionRepository
import org.springframework.stereotype.Service
import java.math.BigDecimal
import java.math.RoundingMode

@Service
class CategoryService(
    private val categoryRepository: CategoryRepository,
    private val transactionRepository: TransactionRepository,
    private val userRepository: UserRepository,
) {
    fun listAll(userId: Long): List<CategoryDTO> =
        categoryRepository.findAllByUserId(userId).map { it.toDto() }

    fun create(userId: Long, dto: CategoryCreateDTO): CategoryDTO {
        if (categoryRepository.existsByUserIdAndNameIgnoreCase(userId, dto.name)) {
            throw DuplicateCategoryNameException("Já existe uma categoria com o nome '${dto.name}'.")
        }
        val user = userRepository.findById(userId)
            .orElseThrow { UserIdDoNotExistsException("Usuário não encontrado.") }
        val category = categoryRepository.save(
            Category(
                user = user,
                name = dto.name,
                flow = dto.flow,
                expenseGroup = dto.expenseGroup,
                openingBalanceAmount = normalizedOpeningBalance(dto.flow, dto.openingBalanceAmount),
            )
        )
        return category.toDto()
    }

    fun update(userId: Long, categoryId: Long, dto: CategoryCreateDTO): CategoryDTO {
        val category = findOrThrow(userId, categoryId)
        if (!category.name.equals(dto.name, ignoreCase = true) &&
            categoryRepository.existsByUserIdAndNameIgnoreCase(userId, dto.name)
        ) {
            throw DuplicateCategoryNameException("Já existe uma categoria com o nome '${dto.name}'.")
        }
        category.name = dto.name
        category.flow = dto.flow
        category.expenseGroup = dto.expenseGroup
        category.openingBalanceAmount = normalizedOpeningBalance(dto.flow, dto.openingBalanceAmount)
        return categoryRepository.save(category).toDto()
    }

    fun delete(userId: Long, categoryId: Long) {
        val category = findOrThrow(userId, categoryId)
        if (transactionRepository.existsByCategoryId(categoryId)) {
            throw CategoryHasTransactionsException(
                "A categoria '${category.name}' possui transações vinculadas e não pode ser excluída."
            )
        }
        categoryRepository.delete(category)
    }

    private fun findOrThrow(userId: Long, categoryId: Long): Category =
        categoryRepository.findByIdAndUserId(categoryId, userId)
            .orElseThrow { CategoryNotFoundException("Categoria não encontrada.") }

    private fun Category.toDto() = CategoryDTO(
        id = id!!,
        name = name,
        flow = flow.name,
        expenseGroup = expenseGroup?.name,
        openingBalanceAmount = openingBalanceAmount,
    )

    private fun normalizedOpeningBalance(flow: Flow, raw: BigDecimal?): BigDecimal {
        if (flow != Flow.investment) {
            return BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP)
        }
        return (raw ?: BigDecimal.ZERO).setScale(2, RoundingMode.HALF_UP)
    }
}
