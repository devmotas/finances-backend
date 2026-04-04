package com.example.finances.services

import com.example.finances.exceptions.UserAlreadyExistsException
import com.example.finances.exceptions.UserIdDoNotExistsException
import com.example.finances.repositories.UserRepository
import com.example.finances.user.User
import com.example.finances.user.UserCreateDTO
import com.example.finances.user.UserDTO
import org.springframework.stereotype.Service

@Service
class UserService(
    private val userRepository: UserRepository,
) {
    fun findById(id: Int): UserDTO {
        val user = userRepository.findById(id)
            .orElseThrow { UserIdDoNotExistsException("Usuário não encontrado") }
        return toDto(user)
    }

    fun findByEmail(email: String): UserDTO {
        val user = userRepository.findByEmail(email)
            .orElseThrow { UserIdDoNotExistsException("Usuário não encontrado") }
        return toDto(user)
    }

    fun createUser(userCreateDTO: UserCreateDTO): UserDTO {
        if (userRepository.findByEmail(userCreateDTO.email).isPresent) {
            throw UserAlreadyExistsException("O e-mail ${userCreateDTO.email} já está em uso.")
        }
        val user = User(
            name = userCreateDTO.name,
            email = userCreateDTO.email,
        )
        val savedUser = userRepository.save(user)
        return findById(savedUser.id!!)
        // TODO salvar senha em userCreateDTO.password
    }

    private fun toDto(user: User): UserDTO = UserDTO(
        id = user.id!!,
        name = user.name,
        email = user.email,
        createdAt = user.createdAt,
        updatedAt = user.updatedAt,
    )
}
