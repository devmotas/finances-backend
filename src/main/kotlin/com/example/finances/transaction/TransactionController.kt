package com.example.finances.transaction

import com.example.finances.security.UserDetailsImpl
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController
import java.time.LocalDate

@RestController
@RequestMapping("/transactions")
class TransactionController(
    private val transactionService: TransactionService,
) {
    @GetMapping
    fun listByMonth(
        @AuthenticationPrincipal user: UserDetailsImpl,
        @RequestParam year: Int,
        @RequestParam month: Int,
    ): List<TransactionDTO> = transactionService.listByMonth(user.id, year, month)

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    fun create(
        @AuthenticationPrincipal user: UserDetailsImpl,
        @RequestBody @Valid dto: TransactionCreateDTO,
    ): TransactionDTO = transactionService.create(user.id, dto)

    @PutMapping("/{id}")
    fun update(
        @AuthenticationPrincipal user: UserDetailsImpl,
        @PathVariable id: Long,
        @RequestParam(defaultValue = "false") applyToFutureSeries: Boolean,
        @RequestBody @Valid dto: TransactionCreateDTO,
    ): TransactionDTO = transactionService.update(user.id, id, dto, applyToFutureSeries)

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun delete(
        @AuthenticationPrincipal user: UserDetailsImpl,
        @PathVariable id: Long,
        @RequestParam(defaultValue = "false") applyToFutureSeries: Boolean,
    ) = transactionService.delete(user.id, id, applyToFutureSeries)
}
