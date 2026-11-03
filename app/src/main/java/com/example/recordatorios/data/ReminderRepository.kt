package com.example.recordatorios.data

import com.example.recordatorios.Reminder

// Contrato: cualquier "depósito" de recordatorios tiene que saber hacer estas 3 cosas.
// "suspend" significa que la operación puede tardar y se ejecuta sin trabar la pantalla.
interface ReminderRepository {
    suspend fun getReminders(): List<Reminder>
    suspend fun getReminderById(id: Int): Reminder?
    suspend fun saveReminder(reminder: Reminder): Reminder
}