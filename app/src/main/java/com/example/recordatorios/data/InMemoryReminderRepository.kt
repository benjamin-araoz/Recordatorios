package com.example.recordatorios.data

import com.example.recordatorios.Reminder
import com.example.recordatorios.initialReminders
import kotlinx.coroutines.delay
import java.io.IOException

// Modos para simular situaciones y poder probar todos los estados de la pantalla
enum class LoadMode { NORMAL, SLOW, EMPTY, ERROR }

object SimulationSettings {
    @Volatile var loadMode: LoadMode = LoadMode.NORMAL
    @Volatile var saveFails: Boolean = false
}

// Implementación del contrato: guarda los recordatorios en una lista en memoria
class InMemoryReminderRepository : ReminderRepository {

    private val reminders = initialReminders.toMutableList()
    private var nextId = (initialReminders.maxOfOrNull { it.id } ?: 0) + 1

    override suspend fun getReminders(): List<Reminder> {
        delay(if (SimulationSettings.loadMode == LoadMode.SLOW) 3000 else 800)
        return when (SimulationSettings.loadMode) {
            LoadMode.ERROR -> throw IOException("No se pudo cargar la lista")
            LoadMode.EMPTY -> emptyList()
            else -> reminders.toList()
        }
    }

    override suspend fun getReminderById(id: Int): Reminder? {
        delay(300)
        return reminders.find { it.id == id }
    }

    override suspend fun saveReminder(reminder: Reminder): Reminder {
        delay(1500)
        if (SimulationSettings.saveFails) throw IOException("No se pudo guardar el recordatorio")
        val saved = reminder.copy(id = nextId++)
        reminders.add(saved)
        return saved
    }
}

// Un único repositorio compartido por toda la app
object AppContainer {
    val reminderRepository: ReminderRepository = InMemoryReminderRepository()
}