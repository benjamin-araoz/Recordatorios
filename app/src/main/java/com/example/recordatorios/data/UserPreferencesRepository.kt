package com.example.recordatorios.data

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException

// Crea el archivo de DataStore una sola vez para toda la app (está fuera de la clase a propósito)
private val Context.dataStore by preferencesDataStore(name = "user_preferences")

// Claves "tipadas": cada una sabe qué tipo de dato guarda (texto, booleano, etc.)
private object PreferenceKeys {
    val THEME_MODE = stringPreferencesKey("theme_mode")
    val SHOW_COMPLETED = booleanPreferencesKey("show_completed")
    val SORT_ORDER = stringPreferencesKey("sort_order")
}

class UserPreferencesRepository(context: Context) {

    private val dataStore = context.dataStore

    // Flow = una pizarra que se actualiza sola cada vez que algo cambia.
    // "catch" recupera un error de lectura (por ejemplo, un archivo corrupto)
    // devolviendo preferencias vacías en vez de romper la app.
    val userPreferences: Flow<UserPreferences> = dataStore.data
        .catch { error ->
            if (error is IOException) emit(androidx.datastore.preferences.core.emptyPreferences())
            else throw error
        }
        .map { prefs ->
            UserPreferences(
                // "enumValueOf" convierte el texto guardado en la opción del enum.
                // Si el texto no existe o es desconocido, se usa el valor por defecto.
                themeMode = prefs[PreferenceKeys.THEME_MODE]?.let {
                    runCatching { ThemeMode.valueOf(it) }.getOrDefault(ThemeMode.SYSTEM)
                } ?: ThemeMode.SYSTEM,
                showCompleted = prefs[PreferenceKeys.SHOW_COMPLETED] ?: true,
                sortOrder = prefs[PreferenceKeys.SORT_ORDER]?.let {
                    runCatching { SortOrder.valueOf(it) }.getOrDefault(SortOrder.PRIORITY)
                } ?: SortOrder.PRIORITY
            )
        }

    // Funciones suspendidas: cada una modifica una sola preferencia, sin trabar la pantalla
    suspend fun setThemeMode(mode: ThemeMode) {
        dataStore.edit { it[PreferenceKeys.THEME_MODE] = mode.name }
    }

    suspend fun setShowCompleted(value: Boolean) {
        dataStore.edit { it[PreferenceKeys.SHOW_COMPLETED] = value }
    }

    suspend fun setSortOrder(order: SortOrder) {
        dataStore.edit { it[PreferenceKeys.SORT_ORDER] = order.name }
    }
}