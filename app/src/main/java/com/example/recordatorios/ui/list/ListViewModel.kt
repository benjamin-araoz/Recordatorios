package com.example.recordatorios.ui.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.recordatorios.Reminder
import com.example.recordatorios.data.LoadMode
import com.example.recordatorios.data.ReminderRepository
import com.example.recordatorios.data.SimulationSettings
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

// Las 4 situaciones posibles del listado. Solo una a la vez (por eso es "sealed").
sealed interface ListUiState {
    data object Loading : ListUiState
    data object Empty : ListUiState
    data class Success(val reminders: List<Reminder>) : ListUiState
    data class Error(val message: String) : ListUiState
}

class ListViewModel(private val repository: ReminderRepository) : ViewModel() {

    // Pizarra modificable: solo el ViewModel puede escribir
    private val _uiState = MutableStateFlow<ListUiState>(ListUiState.Loading)

    // Versión de solo lectura: es la que ve la pantalla
    val uiState: StateFlow<ListUiState> = _uiState.asStateFlow()

    private var loadJob: Job? = null

    // Evento: cargar (o reintentar) la lista
    fun loadReminders() {
        loadJob?.cancel()
        _uiState.update { ListUiState.Loading }
        loadJob = viewModelScope.launch {
            try {
                val reminders = repository.getReminders()
                _uiState.update {
                    if (reminders.isEmpty()) ListUiState.Empty else ListUiState.Success(reminders)
                }
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                _uiState.update { ListUiState.Error(e.message ?: "Ocurrió un error inesperado") }
            }
        }
    }

    // Evento: elegir un modo de simulación para probar los estados
    fun onSimulationSelected(mode: LoadMode) {
        SimulationSettings.loadMode = mode
        loadReminders()
    }
}