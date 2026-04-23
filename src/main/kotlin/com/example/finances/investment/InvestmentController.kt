package com.example.finances.investment

import com.example.finances.security.UserDetailsImpl
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/investments")
class InvestmentController(
    private val investmentService: InvestmentService,
) {
    @GetMapping
    fun getMonthView(
        @AuthenticationPrincipal user: UserDetailsImpl,
        @RequestParam year: Int,
        @RequestParam month: Int,
    ): InvestmentMonthViewDTO = investmentService.monthView(user.id, year, month)
}
