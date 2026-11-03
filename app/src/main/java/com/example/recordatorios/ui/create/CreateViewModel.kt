package com.example.recordatorios.ui.create

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.recordatorios.Priority
import com.example.recordatorios.Reminder
import com.example.recordatorios.ReminderStatus
import com.example.recordatorios.categoryOptions
import com.example.recordatorios.data.ReminderRepository
import com.example.recordatorios.data.SimulationSettings
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

private const val MIN_TITLE_LENGTH = 4
const val MAX_DESCRIPTION_LENGTH = 200

// Todo lo que la pantalla de creación necesita saber, en un solo objeto inmutable
data class CreateUiState(
    val title: String = "",
    val description: String = "",
    val category: String? = null,
    val priority: Priority? = null,
    val titleError: String? = null,
    val descriptionError: String? = null,
    val categoryError: String? = null,
    val priorityError: String? = null,
    val showErrors: Boolean = false,
    val isSubmitting: Boolean = false,
    val saved: Boolean = false,
    val generalError: String? = null
) {
    val isValid: Boolean
        get() = titleError == null && descriptionError == null &&
                categoryError == null && priorityError == null

    // El botón Guardar solo se habilita si es válido y no hay un envío en curso
    val canSave: Boolean
        get() = isValid && !isSubmitting
}

class CreateViewModel(private val repository: ReminderRepository) : ViewModel() {

    private val _uiState = MutableStateFlow(CreateUiState().validated())
    val uiState: StateFlow<CreateUiState> = _uiState.asStateFlow()

    // Eventos que envía la pantalla
    fun onTitleChange(value: String) =
        _uiState.update { it.copy(title = value, showErrors = true).validated() }

    fun onDescriptionChange(value: String) =
        _uiState.update { it.copy(description = value, showErrors = true).validated() }

    fun onCategorySelected(value: String) =
        _uiState.update { it.copy(category = value, showErrors = true).validated() }

    fun onPrioritySelected(value: Priority) =
        _uiState.update { it.copy(priority = value, showErrors = true).validated() }

    fun onSave() {
        val current = _uiState.value
        if (!current.canSave) return
        val category = current.category ?: return
        val priority = current.priority ?: return

        _uiState.update { it.copy(isSubmitting = true, generalError = null) }
        viewModelScope.launch {
            try {
                repository.saveReminder(
                    Reminder(
                        id = 0, // el repositorio asigna el id real
                        title = current.title.trim(),
                        category = category,
                        priority = priority,
                        status = ReminderStatus.Pending,
                        description = current.description.trim().ifEmpty { null }
                    )
                )
                _uiState.update { it.copy(isSubmitting = false, saved = true) }
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isSubmitting = false,
                        generalError = e.message ?: "No se pudo guardar el recordatorio"
                    )
                }
            }
        }
    }

    // Evento: simular que el guardado falla (para probar el error general)
    fun onSimulateSaveFailure(enabled: Boolean) {
        SimulationSettings.saveFails = enabled
    }

    // Reglas de validación: devuelven una copia nueva del estado con los errores actualizados
    private fun CreateUiState.validated(): CreateUiState = copy(
        titleError = if (title.trim().length < MIN_TITLE_LENGTH)
            "El título debe tener al menos $MIN_TITLE_LENGTH caracteres" else null,
        descriptionError = if (description.length > MAX_DESCRIPTION_LENGTH)
            "La descripción no puede superar los $MAX_DESCRIPTION_LENGTH caracteres" else null,
        categoryError = if (category == null || category !in categoryOptions)
            "Elegí una categoría de la lista" else null,
        priorityError = if (priority == null)
            "Elegí una prioridad" else null
    )
}