package com.example.recordatorios.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface ReminderDao {
    @Query("SELECT * FROM reminders WHERE userId = :userId")
    fun getAllReminders(userId: String): Flow<List<ReminderEntity>>

    @Query("SELECT * FROM reminders WHERE id = :id AND userId = :userId")
    fun getReminderById(id: Int, userId: String): Flow<ReminderEntity?>

    @Query("SELECT * FROM reminders WHERE isPendingSync = 1 AND userId = :userId")
    suspend fun getPendingReminders(userId: String): List<ReminderEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertReminder(reminder: ReminderEntity)

    @Query("DELETE FROM reminders WHERE id = :id AND userId = :userId")
    suspend fun deleteReminder(id: Int, userId: String)
}