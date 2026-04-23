package com.example.finances.services

import com.example.finances.exceptions.BadRequestException
import com.example.finances.exceptions.UserIdDoNotExistsException
import com.example.finances.repositories.UserRepository
import com.example.finances.user.PasswordChangeDTO
import com.example.finances.user.User
import com.example.finances.user.UserDTO
import com.example.finances.user.UserMePatchDTO
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class UserService(
    private val userRepository: UserRepository,
    private val passwordEncoder: PasswordEncoder,
) {
    fun findById(id: Long): UserDTO {
        val user = userRepository.findById(id)
            .orElseThrow { UserIdDoNotExistsException("Usuário não encontrado") }
        return toDto(user)
    }

    fun updateDefaultRecurrenceMonths(id: Long, months: Int): UserDTO {
        val user = userRepository.findById(id)
            .orElseThrow { UserIdDoNotExistsException("Usuário não encontrado") }
        user.defaultRecurrenceMonths = months
        return toDto(userRepository.save(user))
    }

    fun patchMe(id: Long, body: UserMePatchDTO): UserDTO {
        if (
            body.defaultRecurrenceMonths == null &&
            body.emergencyFundTargetMonths == null &&
            body.name == null
        ) {
            throw BadRequestException("Informe ao menos um campo para atualizar.")
        }
        val user = userRepository.findById(id)
            .orElseThrow { UserIdDoNotExistsException("Usuário não encontrado") }
        body.name?.let {
            val trimmed = it.trim()
            if (trimmed.length !in 2..120) {
                throw BadRequestException("O nome deve ter entre 2 e 120 caracteres.")
            }
            user.name = trimmed
        }
        body.defaultRecurrenceMonths?.let { user.defaultRecurrenceMonths = it }
        body.emergencyFundTargetMonths?.let { user.emergencyFundTargetMonths = it }
        return toDto(userRepository.save(user))
    }

    @Transactional
    fun changePassword(id: Long, body: PasswordChangeDTO) {
        val user = userRepository.findById(id)
            .orElseThrow { UserIdDoNotExistsException("Usuário não encontrado") }
        if (!passwordEncoder.matches(body.currentPassword, user.password)) {
            throw BadRequestException("Senha atual incorreta.")
        }
        if (passwordEncoder.matches(body.newPassword, user.password)) {
            throw BadRequestException("A nova senha deve ser diferente da atual.")
        }
        val encoded = passwordEncoder.encode(body.newPassword)!!
        val updated = userRepository.updatePasswordById(id, encoded)
        if (updated != 1) {
            throw BadRequestException("Não foi possível atualizar a senha. Tente novamente.")
        }
    }

    private fun toDto(user: User): UserDTO = UserDTO(
        id = user.id!!,
        name = user.name,
        email = user.email,
        createdAt = user.createdAt,
        updatedAt = user.updatedAt,
        defaultRecurrenceMonths = user.defaultRecurrenceMonths,
        emergencyFundTargetMonths = user.emergencyFundTargetMonths,
    )
}
