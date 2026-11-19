package com.example.incidentmonitor.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.incidentmonitor.data.Incident
import com.example.incidentmonitor.data.IncidentRepository
import com.example.incidentmonitor.data.IncidentStatus
import com.example.incidentmonitor.data.Severity
import com.example.incidentmonitor.ui.theme.severityColor
import com.example.incidentmonitor.ui.theme.statusColor

/**
 * Главный экран: название приложения, сводка и основные разделы.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    onOpenList: () -> Unit,
    onOpenForm: () -> Unit,
    onIncidentClick: (Int) -> Unit,
    onOpenOnCall: () -> Unit
) {
    val incidents by IncidentRepository.incidents.collectAsState()

    val total = incidents.size
    val active = incidents.count { it.status != IncidentStatus.RESOLVED }
    val critical = incidents.count {
        it.severity == Severity.CRITICAL && it.status != IncidentStatus.RESOLVED
    }

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Мониторинг ИТ-инцидентов") })
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {
            Text(
                text = "Сводка по состоянию ИТ-сервисов",
                style = MaterialTheme.typography.titleMedium
            )
            Spacer(modifier = Modifier.height(12.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                StatCard(
                    title = "Всего",
                    value = total.toString(),
                    color = Color(0xFF1565C0),
                    modifier = Modifier.weight(1f)
                )
                StatCard(
                    title = "Активных",
                    value = active.toString(),
                    color = Color(0xFFEF6C00),
                    modifier = Modifier.weight(1f)
                )
                StatCard(
                    title = "Критических",
                    value = critical.toString(),
                    color = Color(0xFFC62828),
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            Button(
                onClick = onOpenList,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
            ) {
                Text("Список инцидентов")
            }
            Spacer(modifier = Modifier.height(10.dp))
            OutlinedButton(
                onClick = onOpenForm,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
            ) {
                Text("Сообщить об инциденте")
            }
            Spacer(modifier = Modifier.height(10.dp))
            OutlinedButton(
                onClick = onOpenOnCall,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
            ) {
                Text("Дежурная смена (данные из API)")
            }

            Spacer(modifier = Modifier.height(20.dp))
            Text(
                text = "Последние инциденты",
                style = MaterialTheme.typography.titleMedium
            )
            Spacer(modifier = Modifier.height(8.dp))

            LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                items(incidents.take(3), key = { it.id }) { incident ->
                    IncidentCard(incident = incident, onClick = { onIncidentClick(incident.id) })
                }
            }
        }
    }
}

@Composable
private fun StatCard(
    title: String,
    value: String,
    color: Color,
    modifier: Modifier = Modifier
) {
    ElevatedCard(modifier = modifier) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 14.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = value, fontSize = 30.sp, fontWeight = FontWeight.Bold, color = color)
            Text(text = title, fontSize = 13.sp, color = Color.Gray)
        }
    }
}

/** Карточка инцидента — используется на главном экране и в списке. */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun IncidentCard(incident: Incident, onClick: () -> Unit) {
    ElevatedCard(onClick = onClick, modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = incident.title,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.weight(1f)
                )
                Spacer(modifier = Modifier.size(8.dp))
                Surface(
                    color = severityColor(incident.severity),
                    shape = RoundedCornerShape(6.dp)
                ) {
                    Text(
                        text = incident.severity.label,
                        color = Color.White,
                        fontSize = 12.sp,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                    )
                }
            }
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = "${incident.system} • ${incident.reportedAt}",
                fontSize = 12.sp,
                color = Color.Gray
            )
            Spacer(modifier = Modifier.height(8.dp))
            Surface(
                color = statusColor(incident.status).copy(alpha = 0.15f),
                shape = RoundedCornerShape(6.dp)
            ) {
                Text(
                    text = incident.status.label,
                    color = statusColor(incident.status),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                )
            }
        }
    }
}

/** Общая кнопка "назад" для верхних панелей экранов. */
@Composable
fun BackButton(onBack: () -> Unit) {
    IconButton(onClick = onBack) {
        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Назад")
    }
}
