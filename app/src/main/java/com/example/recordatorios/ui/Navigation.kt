package com.example.recordatorios.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.recordatorios.data.AppContainer
import com.example.recordatorios.ui.auth.AuthScreen
import com.example.recordatorios.ui.create.CreateScreen
import com.example.recordatorios.ui.detail.DetailScreen
import com.example.recordatorios.ui.list.ListScreen
import com.example.recordatorios.ui.settings.SettingsScreen

object Routes {
    const val AUTH = "auth"
    const val LIST = "list"
    const val CREATE = "create"
    const val DETAIL = "detail/{reminderId}"
    const val EDIT = "edit/{reminderId}"
    const val SETTINGS = "settings"
    fun detail(id: Int) = "detail/$id"
    fun edit(id: Int) = "edit/$id"
}

@Composable
fun RecordatoriosNavHost(
    navController: NavHostController = rememberNavController(),
    sessionViewModel: SessionViewModel = viewModel { SessionViewModel(AppContainer.authRepository) }
) {
    val sessionState by sessionViewModel.sessionState.collectAsStateWithLifecycle()

    when (sessionState) {
        // Mientras se consulta por primera vez si hay sesión, no se muestra nada (podría ser
        // un splash/loading; se omite por brevedad)
        is SessionState.Loading -> return

        is SessionState.LoggedOut -> AuthScreen()

        is SessionState.LoggedIn -> {
            NavHost(navController = navController, startDestination = Routes.LIST) {

                composable(Routes.LIST) {
                    ListScreen(
                        onRecordatorioClick = { id -> navController.navigate(Routes.detail(id)) },
                        onCrearRecordatorio = { navController.navigate(Routes.CREATE) },
                        onConfiguracion = { navController.navigate(Routes.SETTINGS) }
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
                        onVolver = { navController.popBackStack() },
                        onEditar = { id -> navController.navigate(Routes.edit(id)) }
                    )
                }

                composable(
                    route = Routes.EDIT,
                    arguments = listOf(navArgument("reminderId") { type = NavType.IntType })
                ) { backStackEntry ->
                    val reminderId = backStackEntry.arguments?.getInt("reminderId") ?: -1
                    CreateScreen(
                        reminderId = reminderId,
                        onGuardar = { navController.popBackStack() },
                        onCancelar = { navController.popBackStack() }
                    )
                }

                composable(Routes.SETTINGS) {
                    SettingsScreen(onVolver = { navController.popBackStack() })
                }
            }
        }
    }
}