package com.example.incidentmonitor.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.incidentmonitor.data.Incident
import com.example.incidentmonitor.data.IncidentRepository
import com.example.incidentmonitor.ui.theme.severityColor
import com.example.incidentmonitor.ui.theme.statusColor
import kotlinx.coroutines.launch

/**
 * Экран просмотра выбранного инцидента со всеми деталями.
 * Отсюда выполняется переход к редактированию.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun IncidentDetailScreen(
    incidentId: Int,
    onBack: () -> Unit,
    onEdit: () -> Unit
) {
    val incidents by IncidentRepository.incidents.collectAsState()
    val incident = incidents.firstOrNull { it.id == incidentId }
    val scope = rememberCoroutineScope()
    var showDeleteDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Просмотр инцидента") },
                navigationIcon = { BackButton(onBack) }
            )
        }
    ) { padding ->
        if (incident == null) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.Center
            ) {
                Text("Инцидент не найден")
            }
            return@Scaffold
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            Text(
                text = incident.title,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(10.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Surface(
                    color = severityColor(incident.severity),
                    shape = RoundedCornerShape(6.dp)
                ) {
                    Text(
                        text = "Критичность: ${incident.severity.label}",
                        color = Color.White,
                        fontSize = 13.sp,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                    )
                }
                Surface(
                    color = statusColor(incident.status).copy(alpha = 0.15f),
                    shape = RoundedCornerShape(6.dp)
                ) {
                    Text(
                        text = incident.status.label,
                        color = statusColor(incident.status),
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
            DetailRow("Затронутая система", incident.system)
            HorizontalDivider()
            DetailRow("Время регистрации", incident.reportedAt)
            HorizontalDivider()
            DetailRow("Описание", incident.description)

            incident.photoPath?.let { photo ->
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Фото с места события",
                    fontSize = 13.sp,
                    color = Color.Gray,
                    style = MaterialTheme.typography.labelSmall
                )
                Spacer(modifier = Modifier.height(6.dp))
                AsyncImage(
                    model = photo,
                    contentDescription = "Фото инцидента",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(240.dp)
                        .clip(RoundedCornerShape(12.dp))
                )
            }

            Spacer(modifier = Modifier.height(24.dp))
            Button(
                onClick = onEdit,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
            ) {
                Text("Редактировать")
            }
            Spacer(modifier = Modifier.height(10.dp))
            OutlinedButton(
                onClick = { showDeleteDialog = true },
                colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFFC62828)),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
            ) {
                Text("Удалить инцидент")
            }
        }

        if (showDeleteDialog) {
            AlertDialog(
                onDismissRequest = { showDeleteDialog = false },
                title = { Text("Удалить инцидент?") },
                text = { Text("«${incident.title}» будет удалён безвозвратно.") },
                confirmButton = {
                    TextButton(onClick = {
                        showDeleteDialog = false
                        val toDelete = incident
                        scope.launch {
                            IncidentRepository.delete(toDelete)
                            onBack()
                        }
                    }) {
                        Text("Удалить", color = Color(0xFFC62828))
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showDeleteDialog = false }) {
                        Text("Отмена")
                    }
                }
            )
        }
    }
}

@Composable
private fun DetailRow(label: String, value: String) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 10.dp)
    ) {
        Text(
            text = label,
            fontSize = 13.sp,
            color = Color.Gray,
            style = MaterialTheme.typography.labelSmall
        )
        Spacer(modifier = Modifier.height(3.dp))
        Text(
            text = value.ifBlank { "—" },
            fontSize = 15.sp
        )
    }
}
