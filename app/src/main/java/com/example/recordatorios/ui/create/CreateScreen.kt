package com.example.recordatorios.ui.create

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.recordatorios.Priority
import com.example.recordatorios.categoryOptions
import com.example.recordatorios.data.AppContainer
import com.example.recordatorios.ui.components.ChoiceChips
import com.example.recordatorios.ui.components.ValidatedTextField
import androidx.compose.material3.Switch
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateScreen(
    onGuardar: () -> Unit,
    onCancelar: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: CreateViewModel = viewModel { CreateViewModel(AppContainer.reminderRepository) }
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    // Cuando el guardado termina con éxito, se avisa a la pantalla anterior
    LaunchedEffect(uiState.saved) {
        if (uiState.saved) onGuardar()
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = { TopAppBar(title = { Text("Nuevo recordatorio") }) }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState())
                .fillMaxWidth()
        ) {
            ValidatedTextField(
                value = uiState.title,
                onValueChange = viewModel::onTitleChange,
                label = "Título",
                errorMessage = if (uiState.showErrors) uiState.titleError else null,
                enabled = !uiState.isSubmitting
            )
            Spacer(modifier = Modifier.height(12.dp))

            ValidatedTextField(
                value = uiState.description,
                onValueChange = viewModel::onDescriptionChange,
                label = "Descripción",
                errorMessage = if (uiState.showErrors) uiState.descriptionError else null,
                helperText = "${uiState.description.length} / $MAX_DESCRIPTION_LENGTH caracteres",
                singleLine = false,
                minLines = 3,
                enabled = !uiState.isSubmitting
            )
            Spacer(modifier = Modifier.height(12.dp))

            ChoiceChips(
                label = "Categoría",
                options = categoryOptions.toList(),
                selected = uiState.category,
                optionLabel = { it },
                onSelected = viewModel::onCategorySelected,
                errorMessage = if (uiState.showErrors) uiState.categoryError else null,
                enabled = !uiState.isSubmitting
            )
            Spacer(modifier = Modifier.height(12.dp))

            ChoiceChips(
                label = "Prioridad",
                options = Priority.entries,
                selected = uiState.priority,
                optionLabel = { it.label },
                onSelected = viewModel::onPrioritySelected,
                errorMessage = if (uiState.showErrors) uiState.priorityError else null,
                enabled = !uiState.isSubmitting
            )
            Spacer(modifier = Modifier.height(12.dp))

            if (uiState.generalError != null) {
                Text(
                    text = "Error: ${uiState.generalError}",
                    color = MaterialTheme.colorScheme.error
                )
                Spacer(modifier = Modifier.height(8.dp))
            }

            var simulateFailure by remember { mutableStateOf(false) }
            Row(verticalAlignment = Alignment.CenterVertically) {
                Switch(checked = simulateFailure, onCheckedChange = {
                    simulateFailure = it
                    viewModel.onSimulateSaveFailure(it)
                })
                Spacer(modifier = Modifier.width(8.dp))
                Text("Simular error al guardar (solo prueba)")
            }
            Spacer(modifier = Modifier.height(12.dp))

            Row {
                OutlinedButton(onClick = onCancelar, enabled = !uiState.isSubmitting) {
                    Text("Cancelar")
                }
                Spacer(modifier = Modifier.width(12.dp))
                Button(onClick = viewModel::onSave, enabled = uiState.canSave) {
                    if (uiState.isSubmitting) {
                        CircularProgressIndicator(modifier = Modifier.size(18.dp), color = Color.White)
                        Spacer(modifier = Modifier.width(8.dp))
                    }
                    Text("Guardar")
                }
            }
        }
    }
}