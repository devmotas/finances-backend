package com.example.finances.services

import com.example.finances.exceptions.UserIdDoNotExistsException
import com.example.finances.repositories.UserRepository
import com.example.finances.user.User
import com.example.finances.user.UserDTO
import org.springframework.stereotype.Service

@Service
class UserService(
    private val userRepository: UserRepository,
) {
    fun findById(id: Long): UserDTO {
        val user = userRepository.findById(id)
            .orElseThrow { UserIdDoNotExistsException("Usuário não encontrado") }
        return toDto(user)
    }

    private fun toDto(user: User): UserDTO = UserDTO(
        id = user.id!!,
        name = user.name,
        email = user.email,
        createdAt = user.createdAt,
        updatedAt = user.updatedAt,
    )
}
