package com.example.finances.controllers

import com.example.finances.services.UserService
import com.example.finances.user.UserDTO
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/users")
class UserController(
    private val userService: UserService,
) {
    @GetMapping("/{id}")
    fun getUserById(@PathVariable id: Long): UserDTO = userService.findById(id)
}
