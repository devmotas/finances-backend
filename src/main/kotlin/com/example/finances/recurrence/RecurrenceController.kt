package com.example.finances.recurrence

import com.example.finances.security.UserDetailsImpl
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/recurrences")
class RecurrenceController(
    private val recurrenceService: RecurrenceService,
) {
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    fun create(
        @AuthenticationPrincipal user: UserDetailsImpl,
        @RequestBody @Valid dto: RecurrenceCreateDTO,
    ): RecurrenceCreatedDTO = recurrenceService.create(user.id, dto)

    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun update(
        @AuthenticationPrincipal user: UserDetailsImpl,
        @PathVariable id: Long,
        @RequestBody @Valid dto: RecurrenceUpdateDTO,
    ) {
        recurrenceService.update(user.id, id, dto)
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun delete(
        @AuthenticationPrincipal user: UserDetailsImpl,
        @PathVariable id: Long,
    ) {
        recurrenceService.deleteFuture(user.id, id)
    }
}
