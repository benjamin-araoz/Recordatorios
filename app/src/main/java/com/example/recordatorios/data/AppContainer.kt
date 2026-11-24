package com.example.recordatorios.data

import android.content.Context
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

object AppContainer {
    private var context: Context? = null

    fun init(appContext: Context) {
        context = appContext
    }

    private val safeContext: Context
        get() = context ?: throw IllegalStateException("AppContainer no inicializado")

    val userPreferencesRepository: UserPreferencesRepository by lazy {
        UserPreferencesRepository(safeContext)
    }

    private val database: AppDatabase by lazy {
        AppDatabase.getDatabase(safeContext)
    }

    val authRepository: AuthRepository by lazy {
        AuthRepository(FirebaseAuth.getInstance())
    }

    private val firestore: FirebaseFirestore by lazy {
        FirebaseFirestore.getInstance()
    }

    val reminderRepository: ReminderRepository by lazy {
        OfflineFirstReminderRepository(database.reminderDao(), firestore, authRepository)
    }
}