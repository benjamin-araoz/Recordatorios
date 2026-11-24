package com.example.recordatorios.data

import com.example.recordatorios.Reminder
import kotlinx.coroutines.flow.Flow

interface ReminderRepository {
    fun getReminders(): Flow<List<Reminder>>
    fun getReminderById(id: Int): Flow<Reminder?>
    suspend fun saveReminder(reminder: Reminder)
    suspend fun updateReminder(reminder: Reminder)
    suspend fun deleteReminder(id: Int)
    suspend fun syncPendingReminders()
}