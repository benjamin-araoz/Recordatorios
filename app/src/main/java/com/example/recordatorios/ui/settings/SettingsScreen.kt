package com.example.recordatorios.ui.settings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.recordatorios.data.AppContainer
import com.example.recordatorios.data.SortOrder
import com.example.recordatorios.data.ThemeMode
import com.example.recordatorios.ui.components.ChoiceChips

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    onVolver: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: SettingsViewModel = viewModel { SettingsViewModel(AppContainer.userPreferencesRepository) }
) {
    val preferences by viewModel.uiState.collectAsStateWithLifecycle()

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = { Text("Configuración") },
                navigationIcon = {
                    IconButton(onClick = onVolver) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(modifier = Modifier.padding(innerPadding).padding(16.dp).fillMaxWidth()) {

            ChoiceChips(
                label = "Tema",
                options = ThemeMode.entries,
                selected = preferences.themeMode,
                optionLabel = {
                    when (it) {
                        ThemeMode.LIGHT -> "Claro"
                        ThemeMode.DARK -> "Oscuro"
                        ThemeMode.SYSTEM -> "Sistema"
                    }
                },
                onSelected = viewModel::onThemeModeSelected
            )
            Spacer(modifier = Modifier.height(32.dp))
            OutlinedButton(
                onClick = {
                    com.example.recordatorios.data.AppContainer.authRepository.signOut()
                    onVolver()
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Cerrar sesión")
            }
            Spacer(modifier = Modifier.height(20.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Mostrar recordatorios completados", style = MaterialTheme.typography.bodyLarge)
                Switch(checked = preferences.showCompleted, onCheckedChange = viewModel::onShowCompletedChanged)
            }
            Spacer(modifier = Modifier.height(20.dp))

            ChoiceChips(
                label = "Ordenar por",
                options = SortOrder.entries,
                selected = preferences.sortOrder,
                optionLabel = {
                    when (it) {
                        SortOrder.PRIORITY -> "Prioridad"
                        SortOrder.TITLE -> "Título"
                    }
                },
                onSelected = viewModel::onSortOrderSelected
            )
        }
    }
}