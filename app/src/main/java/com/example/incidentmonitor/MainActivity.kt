package com.example.incidentmonitor

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.example.incidentmonitor.data.IncidentRepository
import com.example.incidentmonitor.ui.AppNavHost
import com.example.incidentmonitor.ui.theme.IncidentMonitorTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Инициализация локальной базы данных (Room)
        IncidentRepository.init(this)
        setContent {
            IncidentMonitorTheme {
                AppNavHost()
            }
        }
    }
}
