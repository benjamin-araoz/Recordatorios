package com.example.recordatorios.ui.list

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.recordatorios.data.AppContainer
import com.example.recordatorios.ui.components.EmptyState
import com.example.recordatorios.ui.components.ErrorState
import com.example.recordatorios.ui.components.LoadingState
import com.example.recordatorios.ui.components.ReminderCard


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ListScreen(
    onRecordatorioClick: (Int) -> Unit,
    onCrearRecordatorio: () -> Unit,
    onConfiguracion: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: ListViewModel = viewModel {
        ListViewModel(AppContainer.reminderRepository, AppContainer.userPreferencesRepository)
    }
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) { viewModel.loadReminders() }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = { Text("Mis recordatorios") },
                actions = {
                    IconButton(onClick = onConfiguracion) {
                        Icon(Icons.Default.Settings, contentDescription = "Configuración")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = onCrearRecordatorio) {
                Icon(Icons.Default.Add, contentDescription = "Crear recordatorio")
            }
        }
    ) { innerPadding ->
        Column(modifier = Modifier.padding(innerPadding).fillMaxSize()) {
            when (val state = uiState) {
                is ListUiState.Loading -> LoadingState(modifier = Modifier.weight(1f))
                is ListUiState.Empty -> EmptyState(
                    message = "Todavía no existen recordatorios.",
                    actionLabel = "Crear el primero",
                    onAction = onCrearRecordatorio,
                    modifier = Modifier.weight(1f)
                )
                is ListUiState.Error -> ErrorState(
                    message = state.message,
                    onRetry = { viewModel.loadReminders() },
                    modifier = Modifier.weight(1f)
                )
                is ListUiState.Success -> LazyColumn(
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    items(state.reminders) { reminder ->
                        ReminderCard(reminder = reminder, onClick = { onRecordatorioClick(reminder.id) })
                    }
                }
            }
        }
    }
}