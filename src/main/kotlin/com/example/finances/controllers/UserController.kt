package com.example.finances.controllers

import com.example.finances.services.UserService
import com.example.finances.user.UserCreateDTO
import com.example.finances.user.UserDTO
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/users")
class UserController(
    private val userService: UserService,
) {
    @GetMapping("/{id}")
    fun getUserById(@PathVariable id: Int): UserDTO = userService.findById(id)

    @PostMapping
    fun createUser(@RequestBody userCreateDTO: UserCreateDTO): UserDTO =
        userService.createUser(userCreateDTO)
}
