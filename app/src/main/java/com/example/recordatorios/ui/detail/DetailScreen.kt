package com.example.recordatorios.ui.detail

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.recordatorios.data.AppContainer
import com.example.recordatorios.label
import com.example.recordatorios.ui.components.ErrorState
import com.example.recordatorios.ui.components.LoadingState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailScreen(
    reminderId: Int,
    onVolver: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: DetailViewModel = viewModel { DetailViewModel(AppContainer.reminderRepository) }
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(reminderId) { viewModel.loadReminder(reminderId) }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = { Text("Detalle") },
                navigationIcon = {
                    IconButton(onClick = onVolver) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
                    }
                }
            )
        }
    ) { innerPadding ->
        when (val state = uiState) {
            is DetailUiState.Loading -> LoadingState(modifier = Modifier.padding(innerPadding))

            is DetailUiState.NotFound -> ErrorState(
                message = "No se encontró el recordatorio solicitado.",
                onRetry = { viewModel.loadReminder(reminderId) },
                modifier = Modifier.padding(innerPadding)
            )

            is DetailUiState.Error -> ErrorState(
                message = state.message,
                onRetry = { viewModel.loadReminder(reminderId) },
                modifier = Modifier.padding(innerPadding)
            )

            is DetailUiState.Success -> {
                val reminder = state.reminder
                Column(modifier = Modifier.padding(innerPadding).padding(16.dp)) {
                    Text(reminder.title, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(16.dp))
                    Text("Categoría: ${reminder.category}")
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Prioridad: ${reminder.priority.label}")
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Estado: ${reminder.status.label()}")
                    if (reminder.description != null) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("Descripción: ${reminder.description}")
                    }
                }
            }
        }
    }
}