package com.example.recordatorios.data

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface ReminderApiService {
    @GET("reminders")
    suspend fun getReminders(): Response<List<ReminderDto>>

    @POST("reminders")
    suspend fun createReminder(@Body reminder: ReminderDto): Response<ReminderDto>

    @PUT("reminders/{id}")
    suspend fun updateReminder(@Path("id") id: Int, @Body reminder: ReminderDto): Response<ReminderDto>
}