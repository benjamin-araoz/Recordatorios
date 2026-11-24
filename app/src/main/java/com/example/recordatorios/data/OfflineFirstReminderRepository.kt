package com.example.recordatorios.data

import com.example.recordatorios.Priority
import com.example.recordatorios.Reminder
import com.example.recordatorios.ReminderStatus
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await

class OfflineFirstReminderRepository(
    private val reminderDao: ReminderDao,
    private val firestore: FirebaseFirestore,
    private val authRepository: AuthRepository
) : ReminderRepository {

    // La "carpeta" en Firestore propia de cada usuario: users/{uid}/reminders
    private fun remindersCollection(uid: String) =
        firestore.collection("users").document(uid).collection("reminders")

    @OptIn(ExperimentalCoroutinesApi::class)
    override fun getReminders(): Flow<List<Reminder>> =
    // "flatMapLatest" vuelve a armar esta consulta cada vez que cambia la sesión
        // (login o logout). Sin usuario logueado, no hay nada que mostrar.
        authRepository.currentUser.flatMapLatest { user ->
            val uid = user?.uid
            if (uid == null) flowOf(emptyList())
            else reminderDao.getAllReminders(uid).map { entities -> entities.map { it.toReminder() } }
        }

    override fun getReminderById(id: Int): Flow<Reminder?> {
        val uid = authRepository.userId ?: return flowOf(null)
        return reminderDao.getReminderById(id, uid).map { it?.toReminder() }
    }

    override suspend fun saveReminder(reminder: Reminder) {
        val uid = authRepository.userId ?: return
        reminderDao.insertReminder(reminder.toEntity(uid))
    }

    override suspend fun updateReminder(reminder: Reminder) {
        val uid = authRepository.userId ?: return
        reminderDao.insertReminder(reminder.toEntity(uid))
    }

    override suspend fun deleteReminder(id: Int) {
        val uid = authRepository.userId ?: return
        reminderDao.deleteReminder(id, uid)
        // Se intenta borrar también en Firestore. Si falla por falta de conexión,
        // no pasa nada grave: localmente ya no existe, así que no hay nada "pendiente" que mostrar.
        runCatching { remindersCollection(uid).document(id.toString()).delete().await() }
    }

    override suspend fun syncPendingReminders() {
        val uid = authRepository.userId ?: return
        try {
            val pending = reminderDao.getPendingReminders(uid)
            for (entity in pending) {
                val data = mapOf(
                    "title" to entity.title,
                    "description" to entity.description,
                    "category" to entity.category,
                    "priority" to entity.priority,
                    "status" to entity.status
                )
                // "set" reemplaza el documento entero. Subir el mismo id dos veces
                // nunca crea un duplicado, a diferencia de un POST a una API REST.
                remindersCollection(uid).document(entity.id.toString()).set(data).await()
                reminderDao.insertReminder(entity.copy(isPendingSync = false))
            }
        } catch (e: Exception) {
            // Sin conexión o error del servidor: los registros quedan pendientes
            // (isPendingSync = true) y se reintentan en la próxima sincronización.
        }
    }
}

fun ReminderEntity.toReminder(): Reminder {
    val parsedStatus = when {
        this.status == "Pending" -> ReminderStatus.Pending
        this.status == "Done" -> ReminderStatus.Done
        this.status.startsWith("Postponed:") -> ReminderStatus.Postponed(this.status.substringAfter("Postponed:"))
        else -> ReminderStatus.Pending
    }
    return Reminder(
        id = this.id,
        title = this.title,
        description = if (this.description.isBlank()) null else this.description,
        category = this.category,
        priority = Priority.valueOf(this.priority),
        status = parsedStatus
    )
}

fun Reminder.toEntity(userId: String): ReminderEntity {
    val statusString = when (val s = this.status) {
        is ReminderStatus.Pending -> "Pending"
        is ReminderStatus.Done -> "Done"
        is ReminderStatus.Postponed -> "Postponed:${s.until}"
    }
    return ReminderEntity(
        id = this.id,
        userId = userId,
        title = this.title,
        description = this.description ?: "",
        category = this.category,
        priority = this.priority.name,
        status = statusString,
        isPendingSync = true
    )
}