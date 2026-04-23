package com.example.finances.category

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
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/categories")
class CategoryController(
    private val categoryService: CategoryService,
) {
    @GetMapping
    fun listAll(@AuthenticationPrincipal user: UserDetailsImpl): List<CategoryDTO> =
        categoryService.listAll(user.id)

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    fun create(
        @AuthenticationPrincipal user: UserDetailsImpl,
        @RequestBody @Valid dto: CategoryCreateDTO,
    ): CategoryDTO = categoryService.create(user.id, dto)

    @PutMapping("/{id}")
    fun update(
        @AuthenticationPrincipal user: UserDetailsImpl,
        @PathVariable id: Long,
        @RequestBody @Valid dto: CategoryCreateDTO,
    ): CategoryDTO = categoryService.update(user.id, id, dto)

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun delete(
        @AuthenticationPrincipal user: UserDetailsImpl,
        @PathVariable id: Long,
    ) = categoryService.delete(user.id, id)
}
