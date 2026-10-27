package com.example.recordatorios

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.recordatorios.ui.theme.RecordatoriosTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            RecordatoriosTheme {
                ReminderScreen(reminders = sampleReminders)
            }
        }
    }
}

// Pantalla completa: barra superior + botones de filtro + lista que se desplaza
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReminderScreen(reminders: List<Reminder>) {
    // Pizarrón: guarda qué botón está elegido. Arranca en "Todos".
    var selectedFilter by remember { mutableStateOf("Todos") }

    // Según el botón elegido, se decide qué lista mostrar
    val shownReminders = when (selectedFilter) {
        "Pendientes" -> pendingReminders
        "Prioridad alta" -> highPriorityReminders
        else -> reminders
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = { TopAppBar(title = { Text("Mis recordatorios") }) }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier.padding(innerPadding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                Text(
                    text = "Pendientes: ${pendingReminders.size} · " +
                            "Prioridad alta: ${highPriorityReminders.size} · " +
                            "Categorías: ${categories.size}",
                    style = MaterialTheme.typography.bodyMedium
                )
            }
            item {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    listOf("Todos", "Pendientes", "Prioridad alta").forEach { name ->
                        FilterChip(
                            selected = selectedFilter == name,
                            onClick = { selectedFilter = name },
                            label = { Text(name) }
                        )
                    }
                }
            }
            items(shownReminders) { reminder ->
                ReminderItem(reminder)
            }
        }
    }
}

// Una tarjeta por cada recordatorio
@Composable
fun ReminderItem(reminder: Reminder) {
    val categoryColor = categoryColors[reminder.category] ?: Color.Gray

    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(12.dp)
                    .background(categoryColor, CircleShape)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(
                    text = reminder.title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Text("Categoría: ${reminder.category}")
                Text("Prioridad: ${reminder.priority.label}")
                Text("Estado: ${reminder.status.label()}")
                reminder.note?.let {
                    Text(text = it, style = MaterialTheme.typography.bodySmall)
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ReminderScreenPreview() {
    RecordatoriosTheme {
        ReminderScreen(reminders = sampleReminders)
    }
}