package com.example.recordatorios

import android.app.Application
import com.example.recordatorios.data.AppContainer

class RecordatoriosApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        // Inicializamos el contenedor global para que las pantallas puedan usar Room
        AppContainer.init(this)
    }
}