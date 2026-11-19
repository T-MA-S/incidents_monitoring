package com.example.incidentmonitor.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import com.example.incidentmonitor.data.IncidentStatus
import com.example.incidentmonitor.data.Severity

private val LightColors = lightColorScheme(
    primary = Color(0xFF1565C0),
    onPrimary = Color.White,
    primaryContainer = Color(0xFFD6E5FF),
    onPrimaryContainer = Color(0xFF001D3D),
    secondary = Color(0xFF546E7A),
    background = Color(0xFFF7F9FC),
    surface = Color.White
)

private val DarkColors = darkColorScheme(
    primary = Color(0xFF90CAF9),
    primaryContainer = Color(0xFF0D47A1),
    secondary = Color(0xFFB0BEC5)
)

@Composable
fun IncidentMonitorTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = if (darkTheme) DarkColors else LightColors,
        content = content
    )
}

/** Цвет плашки критичности инцидента. */
fun severityColor(severity: Severity): Color = when (severity) {
    Severity.CRITICAL -> Color(0xFFC62828)
    Severity.HIGH -> Color(0xFFEF6C00)
    Severity.MEDIUM -> Color(0xFFF9A825)
    Severity.LOW -> Color(0xFF2E7D32)
}

/** Цвет статуса инцидента. */
fun statusColor(status: IncidentStatus): Color = when (status) {
    IncidentStatus.OPEN -> Color(0xFFC62828)
    IncidentStatus.IN_PROGRESS -> Color(0xFF1565C0)
    IncidentStatus.RESOLVED -> Color(0xFF2E7D32)
}
