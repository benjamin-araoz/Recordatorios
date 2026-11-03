package com.example.recordatorios.ui.list

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.recordatorios.data.AppContainer
import com.example.recordatorios.data.LoadMode
import com.example.recordatorios.ui.components.EmptyState
import com.example.recordatorios.ui.components.ErrorState
import com.example.recordatorios.ui.components.LoadingState
import com.example.recordatorios.ui.components.ReminderCard
import androidx.compose.foundation.layout.Column
import androidx.compose.ui.Alignment

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ListScreen(
    onRecordatorioClick: (Int) -> Unit,
    onCrearRecordatorio: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: ListViewModel = viewModel { ListViewModel(AppContainer.reminderRepository) }
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) { viewModel.loadReminders() }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = { TopAppBar(title = { Text("Mis recordatorios") }) },
        floatingActionButton = {
            FloatingActionButton(onClick = onCrearRecordatorio) {
                Icon(Icons.Default.Add, contentDescription = "Crear recordatorio")
            }
        }
    ) { innerPadding ->
        Column(modifier = Modifier.padding(innerPadding).fillMaxSize()) {
            // Siempre visible, sin importar el estado actual
            Row(
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Simular:")
                SimulationRow(onSelect = { viewModel.onSimulationSelected(it) })
            }

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

@Composable
private fun SimulationRow(onSelect: (LoadMode) -> Unit) {
    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        androidx.compose.material3.TextButton(onClick = { onSelect(LoadMode.NORMAL) }) { Text("Normal") }
        androidx.compose.material3.TextButton(onClick = { onSelect(LoadMode.EMPTY) }) { Text("Vacío") }
        androidx.compose.material3.TextButton(onClick = { onSelect(LoadMode.ERROR) }) { Text("Error") }
    }
}