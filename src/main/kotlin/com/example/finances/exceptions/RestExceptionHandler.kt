package com.example.finances.exceptions

import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice

@RestControllerAdvice
class RestExceptionHandler {

    @ExceptionHandler(UserAlreadyExistsException::class)
    fun handleUserExists(e: UserAlreadyExistsException): ResponseEntity<ErrorResponseDTO> =
        ResponseEntity.status(HttpStatus.CONFLICT)
            .body(ErrorResponseDTO(HttpStatus.CONFLICT.value(), e.message))

    @ExceptionHandler(UserIdDoNotExistsException::class)
    fun handleUserNotFound(e: UserIdDoNotExistsException): ResponseEntity<ErrorResponseDTO> =
        ResponseEntity.status(HttpStatus.NOT_FOUND)
            .body(ErrorResponseDTO(HttpStatus.NOT_FOUND.value(), e.message))

    @ExceptionHandler(InvalidCredentialsException::class)
    fun handleInvalidCredentials(e: InvalidCredentialsException): ResponseEntity<ErrorResponseDTO> =
        ResponseEntity.status(HttpStatus.UNAUTHORIZED)
            .body(ErrorResponseDTO(HttpStatus.UNAUTHORIZED.value(), e.message))

    @ExceptionHandler(MethodArgumentNotValidException::class)
    fun handleValidation(e: MethodArgumentNotValidException): ResponseEntity<ValidationErrorResponseDTO> {
        val fields = e.bindingResult.fieldErrors.map {
            FieldErrorDTO(field = it.field, message = it.defaultMessage ?: "inválido")
        }
        val body = ValidationErrorResponseDTO(
            status = HttpStatus.UNPROCESSABLE_ENTITY.value(),
            message = "Erro de validação",
            fields = fields,
        )
        return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(body)
    }

    @ExceptionHandler(
        CategoryNotFoundException::class,
        TransactionNotFoundException::class,
        RecurrenceNotFoundException::class,
    )
    fun handleNotFound(e: RuntimeException): ResponseEntity<ErrorResponseDTO> =
        ResponseEntity.status(HttpStatus.NOT_FOUND)
            .body(ErrorResponseDTO(HttpStatus.NOT_FOUND.value(), e.message))

    @ExceptionHandler(DuplicateCategoryNameException::class, CategoryHasTransactionsException::class)
    fun handleConflict(e: RuntimeException): ResponseEntity<ErrorResponseDTO> =
        ResponseEntity.status(HttpStatus.CONFLICT)
            .body(ErrorResponseDTO(HttpStatus.CONFLICT.value(), e.message))

    @ExceptionHandler(BadRequestException::class)
    fun handleBadRequest(e: BadRequestException): ResponseEntity<ErrorResponseDTO> =
        ResponseEntity.status(HttpStatus.BAD_REQUEST)
            .body(ErrorResponseDTO(HttpStatus.BAD_REQUEST.value(), e.message))

    @ExceptionHandler(Exception::class)
    fun handleGeneralError(e: Exception): ResponseEntity<ErrorResponseDTO> =
        ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
            .body(ErrorResponseDTO(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Erro interno no servidor."))
}
