package dev.dongeeo.timerfitpro

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.rememberNavController
import dagger.hilt.android.AndroidEntryPoint
import dev.dongeeo.timerfitpro.data.local.dao.ExerciseDao
import dev.dongeeo.timerfitpro.data.local.entity.ExerciseEntity
import dev.dongeeo.timerfitpro.domain.model.ExerciseCategory
import dev.dongeeo.timerfitpro.presentation.navigation.NavGraph
import dev.dongeeo.timerfitpro.presentation.theme.TimerFitTheme
import dev.dongeeo.timerfitpro.presentation.viewmodel.TimerViewModel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    
    @Inject
    lateinit var exerciseDao: ExerciseDao
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        
        Log.d("CICLO_VIDA", "onCreate")
        
        // Poblar base de datos con ejercicios iniciales
        lifecycleScope.launch {
            initializeExercises()
        }
        
        setContent {
            TimerFitTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val navController = rememberNavController()
                    val timerViewModel: TimerViewModel = viewModel()
                    NavGraph(
                        navController = navController,
                        timerViewModel = timerViewModel
                    )
                }
            }
        }
    }
    
    override fun onStart() {
        super.onStart()
        Log.d("CICLO_VIDA", "onStart")
    }
    
    override fun onResume() {
        super.onResume()
        Log.d("CICLO_VIDA", "onResume — retomando si corresponde")
    }
    
    override fun onPause() {
        super.onPause()
        Log.d("CICLO_VIDA", "onPause")
    }
    
    override fun onStop() {
        super.onStop()
        Log.d("CICLO_VIDA", "onStop — temporizador pausado")
    }
    
    override fun onDestroy() {
        super.onDestroy()
        Log.d("CICLO_VIDA", "onDestroy")
    }
    
    private suspend fun initializeExercises() {
        // Verificar si ya hay ejercicios
        val allExercises = exerciseDao.getAllExercises().first()
        val hasExercises = allExercises.isNotEmpty()
        
        // Si no hay ejercicios, insertarlos (lista reducida y mejorada)
        if (!hasExercises) {
            val exercises = listOf(
                // === MULTIARTICULARES (Los más importantes) ===
                ExerciseEntity(
                    id = "squat",
                    name = "Sentadilla",
                    group = "Piernas",
                    iconKey = "dumbbell",
                    category = ExerciseCategory.MULTIARTICULAR.name,
                    description = "Ejercicio fundamental para desarrollar fuerza en piernas y glúteos",
                    muscles = "Cuádriceps,Glúteos,Isquiotibiales",
                    difficulty = "INTERMEDIATE",
                    equipment = "Barra o mancuernas"
                ),
                ExerciseEntity(
                    id = "bench_press",
                    name = "Press banca",
                    group = "Pecho",
                    iconKey = "dumbbell",
                    category = ExerciseCategory.MULTIARTICULAR.name,
                    description = "Ejercicio rey para el desarrollo del pecho, hombros y tríceps",
                    muscles = "Pectorales,Deltoides anterior,Tríceps",
                    difficulty = "INTERMEDIATE",
                    equipment = "Barra o mancuernas"
                ),
                ExerciseEntity(
                    id = "deadlift",
                    name = "Peso muerto",
                    group = "Espalda",
                    iconKey = "dumbbell",
                    category = ExerciseCategory.MULTIARTICULAR.name,
                    description = "Uno de los ejercicios más completos para fuerza funcional",
                    muscles = "Espalda baja,Glúteos,Isquiotibiales,Trapecio",
                    difficulty = "ADVANCED",
                    equipment = "Barra"
                ),
                ExerciseEntity(
                    id = "pull_ups",
                    name = "Dominadas",
                    group = "Espalda",
                    iconKey = "dumbbell",
                    category = ExerciseCategory.MULTIARTICULAR.name,
                    description = "Ejercicio de peso corporal para espalda y bíceps",
                    muscles = "Dorsales,Bíceps,Deltoides posterior",
                    difficulty = "ADVANCED",
                    equipment = "Barra de dominadas"
                ),
                ExerciseEntity(
                    id = "military_press",
                    name = "Press militar",
                    group = "Hombros",
                    iconKey = "dumbbell",
                    category = ExerciseCategory.MULTIARTICULAR.name,
                    description = "Desarrollo completo de hombros y estabilidad del core",
                    muscles = "Deltoides,Tríceps,Core",
                    difficulty = "INTERMEDIATE",
                    equipment = "Barra o mancuernas"
                ),
                
                // === AISLAMIENTO (Los más efectivos) ===
                ExerciseEntity(
                    id = "barbell_curl",
                    name = "Curl con barra",
                    group = "Bíceps",
                    iconKey = "dumbbell",
                    category = ExerciseCategory.ISOLATION.name,
                    description = "Aislamiento efectivo para el desarrollo de bíceps",
                    muscles = "Bíceps braquial",
                    difficulty = "BEGINNER",
                    equipment = "Barra o mancuernas"
                ),
                ExerciseEntity(
                    id = "tricep_extension",
                    name = "Extensión de tríceps",
                    group = "Tríceps",
                    iconKey = "dumbbell",
                    category = ExerciseCategory.ISOLATION.name,
                    description = "Aislamiento para el desarrollo del tríceps",
                    muscles = "Tríceps braquial",
                    difficulty = "BEGINNER",
                    equipment = "Mancuernas o polea"
                ),
                ExerciseEntity(
                    id = "leg_extension",
                    name = "Extensión de cuádriceps",
                    group = "Piernas",
                    iconKey = "dumbbell",
                    category = ExerciseCategory.ISOLATION.name,
                    description = "Aislamiento para el desarrollo de cuádriceps",
                    muscles = "Cuádriceps",
                    difficulty = "BEGINNER",
                    equipment = "Máquina o mancuernas"
                ),
                
                // === CARDIO (Los más populares) ===
                ExerciseEntity(
                    id = "treadmill",
                    name = "Caminadora",
                    group = "Cardio",
                    iconKey = "running",
                    category = ExerciseCategory.CARDIO.name,
                    description = "Cardio de bajo impacto ideal para calentamiento o recuperación",
                    muscles = "Cuádriceps,Glúteos,Pantorrillas",
                    difficulty = "BEGINNER",
                    equipment = "Caminadora"
                ),
                ExerciseEntity(
                    id = "rowing",
                    name = "Remo",
                    group = "Cardio",
                    iconKey = "rowing",
                    category = ExerciseCategory.CARDIO.name,
                    description = "Cardio completo que trabaja todo el cuerpo",
                    muscles = "Espalda,Piernas,Brazos,Core",
                    difficulty = "INTERMEDIATE",
                    equipment = "Máquina de remo"
                ),
                ExerciseEntity(
                    id = "bike",
                    name = "Bicicleta estática",
                    group = "Cardio",
                    iconKey = "bike",
                    category = ExerciseCategory.CARDIO.name,
                    description = "Cardio de bajo impacto para piernas y sistema cardiovascular",
                    muscles = "Cuádriceps,Glúteos,Pantorrillas",
                    difficulty = "BEGINNER",
                    equipment = "Bicicleta estática"
                ),
                
                // === CORE (Los esenciales) ===
                ExerciseEntity(
                    id = "plank",
                    name = "Plancha",
                    group = "Core",
                    iconKey = "yoga",
                    category = ExerciseCategory.CORE.name,
                    description = "Ejercicio isométrico fundamental para fortalecer el core",
                    muscles = "Recto abdominal,Transverso,Oblicuos",
                    difficulty = "INTERMEDIATE",
                    equipment = "Solo peso corporal"
                ),
                ExerciseEntity(
                    id = "crunch",
                    name = "Crunch",
                    group = "Core",
                    iconKey = "yoga",
                    category = ExerciseCategory.CORE.name,
                    description = "Ejercicio básico para el desarrollo del recto abdominal",
                    muscles = "Recto abdominal",
                    difficulty = "BEGINNER",
                    equipment = "Solo peso corporal"
                ),
                
                // === FLEXIBILIDAD (Los básicos) ===
                ExerciseEntity(
                    id = "hamstring_stretch",
                    name = "Estiramiento de isquios",
                    group = "Flexibilidad",
                    iconKey = "stretching",
                    category = ExerciseCategory.FLEXIBILITY.name,
                    description = "Estiramiento esencial para mejorar flexibilidad de piernas",
                    muscles = "Isquiotibiales,Glúteos",
                    difficulty = "BEGINNER",
                    equipment = "Solo peso corporal"
                )
            )
            
            exerciseDao.insertExercises(exercises)
            Log.d("DATABASE", "Ejercicios esenciales insertados: ${exercises.size}")
        }
    }
}
