package com.example.finances.services

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
            )
        )
    }
}
