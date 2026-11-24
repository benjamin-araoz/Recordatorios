package com.example.recordatorios.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "reminders")
data class ReminderEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    // A qué cuenta pertenece este recordatorio. Es la clave para que, si dos usuarios
    // distintos usan el mismo celular, nunca vean los recordatorios del otro.
    val userId: String,
    val title: String,
    val description: String,
    val category: String,
    val priority: String,
    val status: String,
    val isPendingSync: Boolean = false
)