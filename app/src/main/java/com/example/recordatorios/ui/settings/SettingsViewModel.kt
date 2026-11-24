package com.example.recordatorios.ui.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.recordatorios.data.SortOrder
import com.example.recordatorios.data.ThemeMode
import com.example.recordatorios.data.UserPreferences
import com.example.recordatorios.data.UserPreferencesRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class SettingsViewModel(private val repository: UserPreferencesRepository) : ViewModel() {

    // "stateIn" convierte el Flow de DataStore en un StateFlow, con un valor inicial
    // mientras se lee el archivo por primera vez
    val uiState: StateFlow<UserPreferences> = repository.userPreferences
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), UserPreferences())

    fun onThemeModeSelected(mode: ThemeMode) {
        viewModelScope.launch { repository.setThemeMode(mode) }
    }

    fun onShowCompletedChanged(value: Boolean) {
        viewModelScope.launch { repository.setShowCompleted(value) }
    }

    fun onSortOrderSelected(order: SortOrder) {
        viewModelScope.launch { repository.setSortOrder(order) }
    }
}