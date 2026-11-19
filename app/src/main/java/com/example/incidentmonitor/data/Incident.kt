package com.example.incidentmonitor.data

/**
 * Модель инцидента предметной области "Мониторинг ИТ-инцидентов".
 */
data class Incident(
    val id: Int,
    val title: String,
    val description: String,
    val severity: Severity,
    val status: IncidentStatus,
    val system: String,
    val reportedAt: String,
    val photoPath: String? = null
)

enum class Severity(val label: String) {
    CRITICAL("Критический"),
    HIGH("Высокий"),
    MEDIUM("Средний"),
    LOW("Низкий")
}

enum class IncidentStatus(val label: String) {
    OPEN("Открыт"),
    IN_PROGRESS("В работе"),
    RESOLVED("Решён")
}
