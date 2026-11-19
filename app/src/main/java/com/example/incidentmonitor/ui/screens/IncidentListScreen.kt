package com.example.incidentmonitor.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.incidentmonitor.data.IncidentRepository

/**
 * Экран со списком всех инцидентов. Нажатие по карточке открывает просмотр,
 * кнопка "+" — форму добавления нового инцидента.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun IncidentListScreen(
    onIncidentClick: (Int) -> Unit,
    onAddClick: () -> Unit
) {
    val incidents by IncidentRepository.incidents.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Инциденты (${incidents.size})") })
        },
        floatingActionButton = {
            FloatingActionButton(onClick = onAddClick) {
                Icon(Icons.Default.Add, contentDescription = "Новый инцидент")
            }
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .padding(padding)
                .padding(horizontal = 12.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(incidents, key = { it.id }) { incident ->
                IncidentCard(incident = incident, onClick = { onIncidentClick(incident.id) })
            }
        }
    }
}
