package com.example.finances.exceptions

import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice

@RestControllerAdvice
class RestExceptionHandler {
    @ExceptionHandler(UserAlreadyExistsException::class)
    fun handleUserExists(exception: UserAlreadyExistsException): ResponseEntity<ErrorResponseDTO> {
        val error = ErrorResponseDTO(HttpStatus.BAD_REQUEST.value(), exception.message)
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error)
    }

    @ExceptionHandler(UserIdDoNotExistsException::class)
    fun handleUserIdDoNotExists(exception: UserIdDoNotExistsException): ResponseEntity<ErrorResponseDTO> {
        val error = ErrorResponseDTO(HttpStatus.BAD_REQUEST.value(), exception.message)
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error)
    }

    @ExceptionHandler(Exception::class)
    fun handleGeneralError(exception: Exception): ResponseEntity<ErrorResponseDTO> {
        val error = ErrorResponseDTO(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Erro interno no servidor.")
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error)
    }
}
