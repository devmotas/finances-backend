package com.example.finances.services

import com.example.finances.category.Category
import com.example.finances.category.CategoryRepository
import com.example.finances.common.Flow
import com.example.finances.exceptions.InvalidCredentialsException
import com.example.finances.exceptions.UserAlreadyExistsException
import com.example.finances.repositories.UserRepository
import com.example.finances.security.JwtTokenProvider
import com.example.finances.user.AuthResponseDTO
import com.example.finances.user.LoginRequestDTO
import com.example.finances.user.User
import com.example.finances.user.UserCreateDTO
import com.example.finances.user.UserDTO
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service

@Service
class AuthService(
    private val userRepository: UserRepository,
    private val categoryRepository: CategoryRepository,
    private val passwordEncoder: PasswordEncoder,
    private val jwtTokenProvider: JwtTokenProvider,
) {
    fun register(dto: UserCreateDTO): AuthResponseDTO {
        if (userRepository.findByEmail(dto.email).isPresent) {
            throw UserAlreadyExistsException("O e-mail ${dto.email} já está em uso.")
        }
        val user = userRepository.save(
            User(
                name = dto.name,
                email = dto.email,
                password = passwordEncoder.encode(dto.password)!!,
            )
        )
        seedInvestmentCategories(user)
        return buildResponse(user)
    }

    fun login(dto: LoginRequestDTO): AuthResponseDTO {
        val user = userRepository.findByEmail(dto.email)
            .orElseThrow { InvalidCredentialsException("E-mail ou senha inválidos.") }
        if (!passwordEncoder.matches(dto.password, user.password)) {
            throw InvalidCredentialsException("E-mail ou senha inválidos.")
        }
        return buildResponse(user)
    }

    private fun seedInvestmentCategories(user: User) {
        val names = listOf(
            "Renda Fixa (Tesouro, CDB, LCI/LCA)",
            "Renda Variável (Ações, ETFs)",
            "Fundos Imobiliários (FIIs)",
            "Criptomoedas",
            "Poupança",
        )
        for (name in names) {
            categoryRepository.save(
                Category(
                    user = user,
                    name = name,
                    flow = Flow.investment,
                    expenseGroup = null,
                ),
            )
        }
    }

    private fun buildResponse(user: User): AuthResponseDTO {
        val token = jwtTokenProvider.generateToken(user.id!!)
        return AuthResponseDTO(
            token = token,
            user = UserDTO(
                id = user.id!!,
                name = user.name,
                email = user.email,
                createdAt = user.createdAt,
                updatedAt = user.updatedAt,
                defaultRecurrenceMonths = user.defaultRecurrenceMonths,
                emergencyFundTargetMonths = user.emergencyFundTargetMonths,
            )
        )
    }
}
