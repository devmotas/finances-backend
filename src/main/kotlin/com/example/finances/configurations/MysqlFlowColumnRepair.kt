package com.example.finances.configurations

import org.slf4j.LoggerFactory
import org.springframework.boot.context.event.ApplicationReadyEvent
import org.springframework.context.event.EventListener
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.stereotype.Component

@Component
class MysqlFlowColumnRepair(
    private val jdbcTemplate: JdbcTemplate,
) {
    private val log = LoggerFactory.getLogger(javaClass)

    @EventListener(ApplicationReadyEvent::class)
    fun repairFlowColumnsIfNeeded(event: ApplicationReadyEvent) {
        val url = event.applicationContext.environment.getProperty("spring.datasource.url") ?: return
        if (!url.contains("mysql", ignoreCase = true)) {
            return
        }
        try {
            val catType = columnType("categories", "flow")
            val txType = columnType("transactions", "flow")

            fun needsEnumFix(type: String?): Boolean {
                if (type == null) return false
                if (!type.lowercase().startsWith("enum(")) return false
                return !type.contains("investment", ignoreCase = true)
            }

            if (!needsEnumFix(catType) && !needsEnumFix(txType)) {
                return
            }

            log.warn(
                "Ajustando colunas flow de ENUM antigo para VARCHAR(32) (suporte a investment). " +
                    "Em produção você pode executar o mesmo ALTER manualmente antes do deploy.",
            )
            if (needsEnumFix(catType)) {
                jdbcTemplate.execute("ALTER TABLE categories MODIFY COLUMN flow VARCHAR(32) NOT NULL")
            }
            if (needsEnumFix(txType)) {
                jdbcTemplate.execute("ALTER TABLE transactions MODIFY COLUMN flow VARCHAR(32) NOT NULL")
            }
            log.info("Migração de flow concluída (categories e/ou transactions).")
        } catch (e: Exception) {
            log.error(
                "Não foi possível migrar colunas flow para VARCHAR. " +
                    "Execute no MySQL: ALTER TABLE categories MODIFY COLUMN flow VARCHAR(32) NOT NULL; " +
                    "ALTER TABLE transactions MODIFY COLUMN flow VARCHAR(32) NOT NULL; — causa: {}",
                e.message,
            )
        }
    }

    private fun columnType(table: String, column: String): String? {
        val rows = jdbcTemplate.queryForList(
            """
            SELECT COLUMN_TYPE AS col_type FROM INFORMATION_SCHEMA.COLUMNS
            WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = ? AND COLUMN_NAME = ?
            """.trimIndent(),
            table,
            column,
        )
        return rows.firstOrNull()?.get("col_type") as String?
    }
}
