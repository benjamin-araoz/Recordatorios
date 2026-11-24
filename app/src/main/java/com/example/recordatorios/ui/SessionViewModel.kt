package com.example.recordatorios.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.recordatorios.data.AuthRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

// Mientras se consulta a Firebase por primera vez, no sabemos todavía si hay sesión o no
sealed interface SessionState {
    data object Loading : SessionState
    data object LoggedOut : SessionState
    data class LoggedIn(val userId: String) : SessionState
}

class SessionViewModel(repository: AuthRepository) : ViewModel() {

    val sessionState: StateFlow<SessionState> = repository.currentUser
        .map { user ->
            if (user == null) SessionState.LoggedOut else SessionState.LoggedIn(user.uid)
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), SessionState.Loading)
}