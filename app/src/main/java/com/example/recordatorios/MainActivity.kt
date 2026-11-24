package com.example.recordatorios

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.recordatorios.data.AppContainer
import com.example.recordatorios.data.ThemeMode
import com.example.recordatorios.data.UserPreferences
import com.example.recordatorios.ui.RecordatoriosNavHost
import com.example.recordatorios.ui.theme.RecordatoriosTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val preferences by AppContainer.userPreferencesRepository.userPreferences
                .collectAsStateWithLifecycle(initialValue = UserPreferences())

            val useDarkTheme = when (preferences.themeMode) {
                ThemeMode.LIGHT -> false
                ThemeMode.DARK -> true
                ThemeMode.SYSTEM -> isSystemInDarkTheme()
            }

            RecordatoriosTheme(darkTheme = useDarkTheme) {
                RecordatoriosNavHost()
            }
        }
    }
}