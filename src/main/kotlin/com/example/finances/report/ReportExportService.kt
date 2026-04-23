package com.example.finances.report

import com.example.finances.transaction.TransactionRepository
import com.lowagie.text.Document
import com.lowagie.text.Font
import com.lowagie.text.FontFactory
import com.lowagie.text.PageSize
import com.lowagie.text.Paragraph
import com.lowagie.text.Phrase
import com.lowagie.text.pdf.PdfPCell
import com.lowagie.text.pdf.PdfPTable
import com.lowagie.text.pdf.PdfWriter
import org.springframework.stereotype.Service
import java.io.ByteArrayOutputStream
import java.io.StringWriter
import java.nio.charset.StandardCharsets
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter

@Service
class ReportExportService(
    private val transactionRepository: TransactionRepository,
) {
    private val dateFmt = DateTimeFormatter.ISO_LOCAL_DATE

    fun resolveRange(year: Int, month: Int, period: String): Pair<LocalDate, LocalDate> {
        val ym = YearMonth.of(year, month)
        return when (period) {
            "quarter" -> {
                val startMonth = (month - 1) / 3 * 3 + 1
                val startYm = YearMonth.of(year, startMonth)
                val endYm = startYm.plusMonths(2)
                startYm.atDay(1) to endYm.atEndOfMonth()
            }
            "year" -> YearMonth.of(year, 1).atDay(1) to YearMonth.of(year, 12).atEndOfMonth()
            else -> ym.atDay(1) to ym.atEndOfMonth()
        }
    }

    fun buildCsv(userId: Long, from: LocalDate, to: LocalDate): ByteArray {
        val rows = transactionRepository.findAllForExport(userId, from, to)
        val sw = StringWriter()
        sw.append("\uFEFF")
        sw.appendLine("Data;Fluxo;Categoria;Valor;Descrição;Agenda")
        for (t in rows) {
            sw.appendLine(
                listOf(
                    dateFmt.format(t.date),
                    t.flow.name,
                    escapeCsv(t.category.name),
                    t.amount.toPlainString(),
                    escapeCsv(t.description ?: ""),
                    t.schedule.name,
                ).joinToString(";"),
            )
        }
        return sw.toString().toByteArray(StandardCharsets.UTF_8)
    }

    private fun escapeCsv(s: String): String {
        val needs = s.contains(';') || s.contains('"') || s.contains('\n') || s.contains('\r')
        if (!needs) return s
        return '"' + s.replace("\"", "\"\"") + '"'
    }

    fun buildPdf(userId: Long, from: LocalDate, to: LocalDate): ByteArray {
        val rows = transactionRepository.findAllForExport(userId, from, to)
        val out = ByteArrayOutputStream()
        val doc = Document(PageSize.A4.rotate(), 36f, 36f, 48f, 36f)
        PdfWriter.getInstance(doc, out)
        doc.open()
        val titleFont: Font = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 14f)
        val small: Font = FontFactory.getFont(FontFactory.HELVETICA, 10f)
        doc.add(Paragraph("Transações — ${dateFmt.format(from)} a ${dateFmt.format(to)}", titleFont))
        doc.add(Paragraph("Gerado pelo Finances", small))
        doc.add(Paragraph(" ", small))

        val table = PdfPTable(6)
        table.widthPercentage = 100f
        table.setWidths(floatArrayOf(2.2f, 2f, 3.2f, 2f, 5f, 2f))

        fun headerCell(text: String): PdfPCell =
            PdfPCell(Phrase(text, FontFactory.getFont(FontFactory.HELVETICA_BOLD, 9f))).apply {
                paddingBottom = 6f
                paddingTop = 4f
            }

        listOf("Data", "Fluxo", "Categoria", "Valor", "Descrição", "Agenda").forEach {
            table.addCell(headerCell(it))
        }

        val cellFont = FontFactory.getFont(FontFactory.HELVETICA, 8f)
        for (t in rows) {
            table.addCell(PdfPCell(Phrase(dateFmt.format(t.date), cellFont)))
            table.addCell(PdfPCell(Phrase(t.flow.name, cellFont)))
            table.addCell(PdfPCell(Phrase(t.category.name, cellFont)))
            table.addCell(PdfPCell(Phrase(t.amount.toPlainString(), cellFont)))
            table.addCell(PdfPCell(Phrase(t.description ?: "", cellFont)))
            table.addCell(PdfPCell(Phrase(t.schedule.name, cellFont)))
        }
        doc.add(table)
        doc.close()
        return out.toByteArray()
    }
}
