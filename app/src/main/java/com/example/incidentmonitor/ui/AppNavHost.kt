package com.example.incidentmonitor.ui

import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.incidentmonitor.ui.screens.IncidentDetailScreen
import com.example.incidentmonitor.ui.screens.IncidentFormScreen
import com.example.incidentmonitor.ui.screens.IncidentListScreen
import com.example.incidentmonitor.ui.screens.MainScreen
import com.example.incidentmonitor.ui.screens.OnCallScreen

/**
 * Навигация между экранами приложения:
 * main -> list -> detail -> form (редактирование)
 *        |-> form (новый инцидент)
 *        \-> oncall (данные из REST API)
 */
@Composable
fun AppNavHost() {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = "main") {

        composable("main") {
            MainScreen(
                onOpenList = { navController.navigate("list") },
                onOpenForm = { navController.navigate("form") },
                onIncidentClick = { id -> navController.navigate("detail/$id") },
                onOpenOnCall = { navController.navigate("oncall") }
            )
        }

        composable("list") {
            IncidentListScreen(
                onIncidentClick = { id -> navController.navigate("detail/$id") },
                onAddClick = { navController.navigate("form") }
            )
        }

        composable(
            route = "detail/{incidentId}",
            arguments = listOf(navArgument("incidentId") { type = NavType.IntType })
        ) { entry ->
            val incidentId = entry.arguments?.getInt("incidentId") ?: return@composable
            IncidentDetailScreen(
                incidentId = incidentId,
                onBack = { navController.popBackStack() },
                onEdit = { navController.navigate("form?incidentId=$incidentId") }
            )
        }

        composable(
            route = "form?incidentId={incidentId}",
            arguments = listOf(navArgument("incidentId") {
                type = NavType.IntType
                defaultValue = -1
            })
        ) { entry ->
            val incidentId = entry.arguments?.getInt("incidentId")
            IncidentFormScreen(
                incidentId = incidentId?.takeIf { it != -1 },
                onBack = { navController.popBackStack() },
                onSaved = { navController.popBackStack() }
            )
        }

        composable("oncall") {
            OnCallScreen(onBack = { navController.popBackStack() })
        }
    }
}
