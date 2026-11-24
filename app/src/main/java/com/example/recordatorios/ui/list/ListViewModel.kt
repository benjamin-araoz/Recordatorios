package com.example.recordatorios.ui.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.recordatorios.Reminder
import com.example.recordatorios.ReminderStatus
import com.example.recordatorios.data.ReminderRepository
import com.example.recordatorios.data.SortOrder
import com.example.recordatorios.data.UserPreferencesRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

sealed interface ListUiState {
    data object Loading : ListUiState
    data object Empty : ListUiState
    data class Success(val reminders: List<Reminder>) : ListUiState
    data class Error(val message: String) : ListUiState
}

class ListViewModel(
    private val repository: ReminderRepository,
    private val preferencesRepository: UserPreferencesRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<ListUiState>(ListUiState.Loading)
    val uiState: StateFlow<ListUiState> = _uiState.asStateFlow()

    init {
        loadReminders()
        syncRemindersConInternet()
    }

    fun loadReminders() {
        viewModelScope.launch {
            try {
                // "combine" une los dos Flow: cada vez que cambian los recordatorios (Room)
                // O cambian las preferencias (DataStore), se recalcula la lista mostrada
                repository.getReminders()
                    .combine(preferencesRepository.userPreferences) { reminders, prefs ->
                        reminders
                            .filter { prefs.showCompleted || it.status !is ReminderStatus.Done }
                            .let { list ->
                                when (prefs.sortOrder) {
                                    SortOrder.PRIORITY -> list.sortedByDescending { it.priority.ordinal }
                                    SortOrder.TITLE -> list.sortedBy { it.title.lowercase() }
                                }
                            }
                    }
                    .collect { reminders ->
                        _uiState.update {
                            if (reminders.isEmpty()) ListUiState.Empty else ListUiState.Success(reminders)
                        }
                    }
            } catch (e: Exception) {
                _uiState.update { ListUiState.Error(e.message ?: "Ocurrió un error inesperado") }
            }
        }
    }

    private fun syncRemindersConInternet() {
        viewModelScope.launch { repository.syncPendingReminders() }
    }

    fun onSimulationSelected(mode: Any) { }
}