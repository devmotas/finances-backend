package com.example.finances.summary

import com.example.finances.security.UserDetailsImpl
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/finance/summary")
class FinanceSummaryController(
    private val financeSummaryService: FinanceSummaryService,
) {
    @GetMapping
    fun get(
        @AuthenticationPrincipal user: UserDetailsImpl,
        @RequestParam year: Int,
        @RequestParam month: Int,
    ): FinanceSummaryDTO = financeSummaryService.summary(user.id, year, month)
}
