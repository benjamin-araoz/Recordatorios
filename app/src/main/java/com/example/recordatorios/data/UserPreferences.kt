package com.example.recordatorios.data

// Las dos formas de ordenar el listado
enum class SortOrder { PRIORITY, TITLE }

// Las 3 opciones de tema
enum class ThemeMode { LIGHT, DARK, SYSTEM }

// Una "foto" de todas las preferencias actuales, con valores por defecto
// para cuando todavía no se guardó nada
data class UserPreferences(
    val themeMode: ThemeMode = ThemeMode.SYSTEM,
    val showCompleted: Boolean = true,
    val sortOrder: SortOrder = SortOrder.PRIORITY
)