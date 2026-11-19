package com.example.incidentmonitor.data

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Локальное хранение: таблица инцидентов в SQLite (Room).
 * Перечисления критичности и статуса сохраняются как строки.
 */
@Entity(tableName = "incidents")
data class IncidentEntity(
    @PrimaryKey val id: Int,
    val title: String,
    val description: String,
    val severity: String,
    val status: String,
    val system: String,
    val reportedAt: String,
    val photoPath: String? = null
)

fun IncidentEntity.toDomain() = Incident(
    id = id,
    title = title,
    description = description,
    severity = Severity.valueOf(severity),
    status = IncidentStatus.valueOf(status),
    system = system,
    reportedAt = reportedAt,
    photoPath = photoPath
)

fun Incident.toEntity() = IncidentEntity(
    id = id,
    title = title,
    description = description,
    severity = severity.name,
    status = status.name,
    system = system,
    reportedAt = reportedAt,
    photoPath = photoPath
)
