package com.example.incidentmonitor.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.incidentmonitor.data.api.ApiClient
import com.example.incidentmonitor.data.api.ApiUser
import kotlinx.coroutines.launch

/** Состояние загрузки данных из REST API. */
sealed interface ApiState {
    data object Loading : ApiState
    data class Error(val message: String) : ApiState
    data class Success(val users: List<ApiUser>) : ApiState
}

/**
 * Экран «Дежурная смена»: данные загружаются из внешнего REST API
 * (jsonplaceholder.typicode.com). Отображаются состояния загрузки и ошибки.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OnCallScreen(onBack: () -> Unit) {
    var state by remember { mutableStateOf<ApiState>(ApiState.Loading) }
    val scope = rememberCoroutineScope()

    fun load() {
        state = ApiState.Loading
        scope.launch {
            state = try {
                ApiState.Success(ApiClient.service.getUsers())
            } catch (e: Exception) {
                ApiState.Error(
                    "Не удалось получить данные от сервера.\nПроверьте подключение к интернету."
                )
            }
        }
    }

    LaunchedEffect(Unit) { load() }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Дежурная смена") },
                navigationIcon = { BackButton(onBack) }
            )
        }
    ) { padding ->
        when (val s = state) {
            is ApiState.Loading -> Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    CircularProgressIndicator()
                    Spacer(modifier = Modifier.height(12.dp))
                    Text("Загрузка данных...", color = Color.Gray)
                }
            }

            is ApiState.Error -> Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text("⚠ Ошибка сети", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                Spacer(modifier = Modifier.height(8.dp))
                Text(s.message, color = Color.Gray, fontSize = 14.sp)
                Spacer(modifier = Modifier.height(16.dp))
                Button(onClick = { load() }) {
                    Text("Повторить")
                }
            }

            is ApiState.Success -> LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                item {
                    Text(
                        text = "Данные получены из REST API: jsonplaceholder.typicode.com",
                        fontSize = 12.sp,
                        color = Color.Gray,
                        style = MaterialTheme.typography.labelSmall,
                        modifier = Modifier.padding(bottom = 4.dp)
                    )
                }
                items(s.users, key = { it.id }) { user ->
                    OnCallCard(user)
                }
            }
        }
    }
}

@Composable
private fun OnCallCard(user: ApiUser) {
    ElevatedCard(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(14.dp)) {
            Text(text = user.name, fontWeight = FontWeight.SemiBold)
            Text(text = user.company.name, fontSize = 13.sp, color = Color(0xFF1565C0))
            Spacer(modifier = Modifier.height(6.dp))
            Text(text = "✉ ${user.email}", fontSize = 13.sp, color = Color.Gray)
            Text(text = "☎ ${user.phone}", fontSize = 13.sp, color = Color.Gray)
        }
    }
}
