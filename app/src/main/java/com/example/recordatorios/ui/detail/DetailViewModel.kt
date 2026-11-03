package com.example.recordatorios.ui.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.recordatorios.Reminder
import com.example.recordatorios.data.ReminderRepository
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

sealed interface DetailUiState {
    data object Loading : DetailUiState
    data class Success(val reminder: Reminder) : DetailUiState
    data object NotFound : DetailUiState
    data class Error(val message: String) : DetailUiState
}

class DetailViewModel(private val repository: ReminderRepository) : ViewModel() {

    private val _uiState = MutableStateFlow<DetailUiState>(DetailUiState.Loading)
    val uiState: StateFlow<DetailUiState> = _uiState.asStateFlow()

    // Evento: buscar el recordatorio por su identificador
    fun loadReminder(id: Int) {
        _uiState.update { DetailUiState.Loading }
        viewModelScope.launch {
            try {
                val reminder = repository.getReminderById(id)
                _uiState.update {
                    if (reminder == null) DetailUiState.NotFound else DetailUiState.Success(reminder)
                }
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                _uiState.update { DetailUiState.Error(e.message ?: "No se pudo cargar el recordatorio") }
            }
        }
    }
}