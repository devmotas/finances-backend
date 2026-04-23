package com.example.finances.controllers

import com.example.finances.security.UserDetailsImpl
import com.example.finances.services.UserService
import com.example.finances.user.PasswordChangeDTO
import com.example.finances.user.UserDTO
import com.example.finances.user.UserMePatchDTO
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PatchMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/users/me")
class UserMeController(
    private val userService: UserService,
) {
    @GetMapping
    fun me(@AuthenticationPrincipal user: UserDetailsImpl): UserDTO =
        userService.findById(user.id)

    @PatchMapping
    fun patchMe(
        @AuthenticationPrincipal user: UserDetailsImpl,
        @RequestBody @Valid body: UserMePatchDTO,
    ): UserDTO = userService.patchMe(user.id, body)

    @PostMapping("/password")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun changePassword(
        @AuthenticationPrincipal user: UserDetailsImpl,
        @RequestBody @Valid body: PasswordChangeDTO,
    ) {
        userService.changePassword(user.id, body)
    }
}
