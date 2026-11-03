package com.example.recordatorios

import androidx.compose.ui.graphics.Color

// Menú cerrado de prioridades
enum class Priority(val label: String) {
    LOW("Baja"),
    MEDIUM("Media"),
    HIGH("Alta")
}

// Estados posibles: Postponed lleva un dato extra (hasta cuándo)
sealed class ReminderStatus {
    object Pending : ReminderStatus()
    object Done : ReminderStatus()
    data class Postponed(val until: String) : ReminderStatus()
}

// La "ficha" de cada recordatorio (inmutable, description es opcional)
data class Reminder(
    val id: Int,
    val title: String,
    val category: String,
    val priority: Priority,
    val status: ReminderStatus,
    val description: String? = null
)

// Convierte el estado en un texto para mostrar en pantalla
fun ReminderStatus.label(): String = when (this) {
    is ReminderStatus.Pending -> "Pendiente"
    is ReminderStatus.Done -> "Hecho"
    is ReminderStatus.Postponed -> "Pospuesto hasta ${this.until}"
}

// Opciones de categoría definidas (el formulario solo acepta estas)
val categoryOptions: Set<String> = setOf("Trabajo", "Casa", "Estudio", "Salud")

// Map: cada categoría asociada a un color
val categoryColors: Map<String, Color> = mapOf(
    "Trabajo" to Color(0xFF1E88E5),
    "Casa" to Color(0xFF43A047),
    "Estudio" to Color(0xFFE53935),
    "Salud" to Color(0xFF8E24AA)
)

// Datos de arranque del repositorio en memoria
val initialReminders: List<Reminder> = listOf(
    Reminder(1, "Entregar informe mensual", "Trabajo", Priority.HIGH, ReminderStatus.Pending, "Enviar por mail al jefe"),
    Reminder(2, "Reunión de equipo", "Trabajo", Priority.MEDIUM, ReminderStatus.Done),
    Reminder(3, "Comprar verduras", "Casa", Priority.LOW, ReminderStatus.Pending),
    Reminder(4, "Pagar el alquiler", "Casa", Priority.HIGH, ReminderStatus.Postponed("viernes")),
    Reminder(5, "Estudiar para el parcial", "Estudio", Priority.HIGH, ReminderStatus.Pending, "Capítulos 3 y 4"),
    Reminder(6, "Turno con el dentista", "Salud", Priority.MEDIUM, ReminderStatus.Postponed("próxima semana")),
    Reminder(7, "Tomar agua", "Salud", Priority.LOW, ReminderStatus.Done)
)