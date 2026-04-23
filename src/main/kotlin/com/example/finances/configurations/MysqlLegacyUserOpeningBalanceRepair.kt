package com.example.finances.configurations

import org.slf4j.LoggerFactory
import org.springframework.boot.context.event.ApplicationReadyEvent
import org.springframework.context.event.EventListener
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.stereotype.Component

@Component
class MysqlLegacyUserOpeningBalanceRepair(
    private val jdbcTemplate: JdbcTemplate,
) {
    private val log = LoggerFactory.getLogger(javaClass)

    @EventListener(ApplicationReadyEvent::class)
    fun dropLegacyOpeningBalanceColumnIfNeeded(event: ApplicationReadyEvent) {
        val url = event.applicationContext.environment.getProperty("spring.datasource.url") ?: return
        if (!url.contains("mysql", ignoreCase = true)) {
            return
        }

        try {
            val exists = columnExists("users", "opening_balance_amount")
            if (!exists) {
                return
            }

            log.warn(
                "Coluna legada users.opening_balance_amount detectada. " +
                    "Removendo automaticamente para manter consistencia com o modelo atual.",
            )
            jdbcTemplate.execute("ALTER TABLE users DROP COLUMN opening_balance_amount")
            log.info("Coluna legada users.opening_balance_amount removida com sucesso.")
        } catch (e: Exception) {
            log.error(
                "Nao foi possivel remover users.opening_balance_amount automaticamente. " +
                    "Execute no MySQL: ALTER TABLE users DROP COLUMN opening_balance_amount; — causa: {}",
                e.message,
            )
        }
    }

    private fun columnExists(table: String, column: String): Boolean {
        val rows = jdbcTemplate.queryForList(
            """
            SELECT 1
            FROM INFORMATION_SCHEMA.COLUMNS
            WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = ? AND COLUMN_NAME = ?
            LIMIT 1
            """.trimIndent(),
            table,
            column,
        )
        return rows.isNotEmpty()
    }
}
