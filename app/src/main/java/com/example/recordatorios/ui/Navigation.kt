package com.example.recordatorios.ui

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.recordatorios.ui.create.CreateScreen
import com.example.recordatorios.ui.detail.DetailScreen
import com.example.recordatorios.ui.list.ListScreen

// Nombres de las rutas del grafo de navegación
object Routes {
    const val LIST = "list"
    const val CREATE = "create"
    const val DETAIL = "detail/{reminderId}"
    fun detail(id: Int) = "detail/$id"
}

@Composable
fun RecordatoriosNavHost(navController: NavHostController = rememberNavController()) {
    NavHost(navController = navController, startDestination = Routes.LIST) {

        composable(Routes.LIST) {
            ListScreen(
                onRecordatorioClick = { id -> navController.navigate(Routes.detail(id)) },
                onCrearRecordatorio = { navController.navigate(Routes.CREATE) }
            )
        }

        composable(Routes.CREATE) {
            CreateScreen(
                onGuardar = { navController.popBackStack() },
                onCancelar = { navController.popBackStack() }
            )
        }

        composable(
            route = Routes.DETAIL,
            arguments = listOf(navArgument("reminderId") { type = NavType.IntType })
        ) { backStackEntry ->
            val reminderId = backStackEntry.arguments?.getInt("reminderId") ?: -1
            DetailScreen(
                reminderId = reminderId,
                onVolver = { navController.popBackStack() }
            )
        }
    }
}