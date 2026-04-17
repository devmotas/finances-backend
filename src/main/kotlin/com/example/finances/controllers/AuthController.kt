package com.example.finances.controllers

import com.example.finances.services.AuthService
import com.example.finances.user.AuthResponseDTO
import com.example.finances.user.LoginRequestDTO
import com.example.finances.user.UserCreateDTO
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/auth")
class AuthController(
    private val authService: AuthService,
) {
    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    fun register(@RequestBody @Valid dto: UserCreateDTO): AuthResponseDTO =
        authService.register(dto)

    @PostMapping("/login")
    fun login(@RequestBody @Valid dto: LoginRequestDTO): AuthResponseDTO =
        authService.login(dto)
}
