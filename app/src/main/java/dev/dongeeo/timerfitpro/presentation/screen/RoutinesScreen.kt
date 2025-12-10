package dev.dongeeo.timerfitpro.presentation.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.dongeeo.timerfitpro.domain.model.Exercise

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RoutinesScreen(
    onBack: () -> Unit,
    onExerciseSelected: (Exercise) -> Unit
) {
    val routines = listOf(
        Routine(
            name = "Principiante - Full Body",
            description = "Rutina completa para empezar",
            exercises = listOf("Sentadilla", "Press banca", "Remo", "Plancha", "Caminadora")
        ),
        Routine(
            name = "Avanzado - Torso",
            description = "Enfoque en tren superior",
            exercises = listOf("Press inclinado", "Dominadas", "Press militar", "Curl bíceps")
        ),
        Routine(
            name = "Ganar Masa",
            description = "Fuerza e hipertrofia",
            exercises = listOf("Peso muerto", "Sentadilla", "Press banca", "Remo pendlay")
        ),
        Routine(
            name = "Definición",
            description = "HIIT y cardio",
            exercises = listOf("Circuito HIIT", "Remo", "Sentadilla aire", "Plancha")
        )
    )
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Rutinas") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Text("←")
                    }
                }
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(routines) { routine ->
                RoutineCard(routine = routine)
            }
        }
    }
}

@Composable
fun RoutineCard(routine: Routine) {
    val routineEmoji = when (routine.name) {
        "Principiante - Full Body" -> "🟦"
        "Avanzado - Torso" -> "🟥"
        "Ganar Masa" -> "🟩"
        "Definición" -> "🟧"
        else -> "💪"
    }
    
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 4.dp,
            pressedElevation = 8.dp
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = routineEmoji,
                    fontSize = 32.sp
                )
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = routine.name,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = routine.description,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            HorizontalDivider()
            
            Spacer(modifier = Modifier.height(12.dp))
            
            Text(
                text = "Ejercicios:",
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.SemiBold
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            routine.exercises.forEach { exercise ->
                Row(
                    modifier = Modifier.padding(vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "• ",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = exercise,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        }
    }
}

data class Routine(
    val name: String,
    val description: String,
    val exercises: List<String>
)

