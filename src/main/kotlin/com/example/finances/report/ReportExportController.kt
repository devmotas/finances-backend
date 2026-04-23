package com.example.finances.report

import com.example.finances.exceptions.BadRequestException
import com.example.finances.security.UserDetailsImpl
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
@RestController
@RequestMapping("/reports")
class ReportExportController(
    private val reportExportService: ReportExportService,
) {
    @GetMapping("/export")
    fun export(
        @AuthenticationPrincipal user: UserDetailsImpl,
        @RequestParam format: String,
        @RequestParam year: Int,
        @RequestParam month: Int,
        @RequestParam(defaultValue = "month") period: String,
    ): ResponseEntity<ByteArray> {
        val normalizedPeriod = period.lowercase()
        if (normalizedPeriod !in setOf("month", "quarter", "year")) {
            throw BadRequestException("Período inválido. Use month, quarter ou year.")
        }
        val (from, to) = reportExportService.resolveRange(year, month, normalizedPeriod)
        val label = "${from.year}-${String.format("%02d", from.monthValue)}_${to.year}-${String.format("%02d", to.monthValue)}"

        return when (format.lowercase()) {
            "csv" -> {
                val bytes = reportExportService.buildCsv(user.id, from, to)
                val filename = "transacoes-$label.csv"
                ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType("text/csv;charset=UTF-8"))
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"$filename\"")
                    .body(bytes)
            }
            "pdf" -> {
                val bytes = reportExportService.buildPdf(user.id, from, to)
                val filename = "transacoes-$label.pdf"
                ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_PDF)
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"$filename\"")
                    .body(bytes)
            }
            else -> throw BadRequestException("Formato inválido. Use csv ou pdf.")
        }
    }
}
