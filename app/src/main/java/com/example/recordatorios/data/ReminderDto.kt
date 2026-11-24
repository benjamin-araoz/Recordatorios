package com.example.recordatorios.data

// El DTO (Data Transfer Object), quedó documentado como referencia pero ya no se usa
// para transformar datos remotos: con Firestore, la sincronización trabaja directo
// con mapas de clave-valor (ver OfflineFirstReminderRepository.syncPendingReminders).
data class ReminderDto(
    val id: Int,
    val title: String,
    val description: String,
    val category: String,
    val priority: String,
    val status: String
)