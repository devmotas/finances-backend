package com.example.finances.security

import com.example.finances.repositories.UserRepository
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.stereotype.Service

@Service
class UserDetailsServiceImpl(
    private val userRepository: UserRepository,
) : UserDetailsService {

    override fun loadUserByUsername(email: String): UserDetailsImpl =
        userRepository.findByEmail(email)
            .map { UserDetailsImpl(it) }
            .orElseThrow { UsernameNotFoundException("Usuário não encontrado: $email") }

    fun loadUserById(id: Long): UserDetailsImpl =
        userRepository.findById(id)
            .map { UserDetailsImpl(it) }
            .orElseThrow { UsernameNotFoundException("Usuário não encontrado: id=$id") }
}
