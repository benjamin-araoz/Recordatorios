package com.example.recordatorios.ui.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.recordatorios.data.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

// true = pantalla de registro, false = pantalla de login (comparten el mismo formulario)
data class AuthUiState(
    val email: String = "",
    val password: String = "",
    val isRegisterMode: Boolean = false,
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)

class AuthViewModel(private val repository: AuthRepository) : ViewModel() {

    private val _uiState = MutableStateFlow(AuthUiState())
    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()

    fun onEmailChange(value: String) = _uiState.update { it.copy(email = value, errorMessage = null) }
    fun onPasswordChange(value: String) = _uiState.update { it.copy(password = value, errorMessage = null) }
    fun onToggleMode() = _uiState.update {
        it.copy(isRegisterMode = !it.isRegisterMode, errorMessage = null)
    }

    fun onSubmit() {
        val current = _uiState.value
        if (current.email.isBlank() || current.password.length < 6) {
            _uiState.update { it.copy(errorMessage = "Ingresá un email válido y una contraseña de al menos 6 caracteres") }
            return
        }

        _uiState.update { it.copy(isLoading = true, errorMessage = null) }
        viewModelScope.launch {
            val result = if (current.isRegisterMode) {
                repository.signUp(current.email.trim(), current.password)
            } else {
                repository.signIn(current.email.trim(), current.password)
            }
            // Si sale bien, no hace falta hacer nada más acá: el Flow de currentUser
            // (en el repositorio) va a avisar solo a la navegación que hay sesión.
            result.onFailure { error ->
                _uiState.update {
                    it.copy(isLoading = false, errorMessage = traducirError(error))
                }
            }
        }
    }

    // Traduce los mensajes técnicos de Firebase a algo entendible
    private fun traducirError(error: Throwable): String {
        val mensaje = error.message ?: ""
        return when {
            mensaje.contains("badly formatted") -> "El email no tiene un formato válido"
            mensaje.contains("already in use") -> "Ya existe una cuenta con ese email"
            mensaje.contains("no user record") -> "No existe una cuenta con ese email"
            mensaje.contains("password is invalid") -> "La contraseña es incorrecta"
            mensaje.contains("network") -> "No hay conexión a internet"
            else -> "No se pudo completar la operación. Intentá de nuevo"
        }
    }
    fun resetState() {
        _uiState.update { AuthUiState(isRegisterMode = it.isRegisterMode) }
    }
}