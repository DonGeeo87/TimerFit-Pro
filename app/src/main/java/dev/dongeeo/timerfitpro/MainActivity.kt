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

/**
 * MainActivity - Actividad principal de la aplicación
 * 
 * CONCEPTOS CLAVE:
 * 
 * 1. @AndroidEntryPoint: Anotación de Hilt que permite inyección de dependencias
 *    - Hilt inyecta automáticamente las dependencias marcadas con @Inject
 *    - Debe estar en la clase Application también (TimerFitApplication)
 * 
 * 2. ComponentActivity: Actividad base para Jetpack Compose
 *    - Reemplaza a AppCompatActivity cuando usas solo Compose
 *    - Proporciona setContent {} para definir la UI
 * 
 * 3. lifecycleScope: Coroutine scope vinculado al ciclo de vida de la Activity
 *    - Se cancela automáticamente cuando la Activity se destruye
 *    - Útil para operaciones asíncronas que deben vivir mientras la Activity existe
 * 
 * 4. viewModel(): Obtiene una instancia del ViewModel compartida a nivel de Activity
 *    - Esta instancia se comparte entre todas las pantallas (screens)
 *    - Permite que el ejercicio seleccionado persista entre navegaciones
 */
@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    
    // INYECCIÓN DE DEPENDENCIAS con Hilt
    // @Inject: Hilt inyecta automáticamente el ExerciseDao
    // lateinit: Se inicializa después del constructor (por Hilt)
    @Inject
    lateinit var exerciseDao: ExerciseDao
    
    /**
     * onCreate() - Se llama cuando la Activity se crea
     * 
     * CICLO DE VIDA:
     * onCreate() → onStart() → onResume() → [App activa] → onPause() → onStop() → onDestroy()
     * 
     * IMPORTANTE: En rotación de pantalla, onCreate() se llama de nuevo,
     * pero el ViewModel NO se destruye (esa es la magia de ViewModel)
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // EdgeToEdge: UI que se extiende hasta los bordes de la pantalla
        // (debajo de la barra de estado y navegación)
        enableEdgeToEdge()
        
        Log.d("CICLO_VIDA", "onCreate")
        
        // POBLAR BASE DE DATOS: Operación asíncrona
        // lifecycleScope.launch: Inicia una coroutine que vive mientras la Activity existe
        // initializeExercises() es una función suspend (operación de base de datos)
        lifecycleScope.launch {
            initializeExercises()
        }
        
        // setContent: Define la UI con Jetpack Compose
        // Todo lo que está dentro se convierte en la UI de la Activity
        setContent {
            // TimerFitTheme: Tema personalizado de la app (colores, tipografía, etc.)
            TimerFitTheme {
                // Surface: Contenedor base con fondo
                Surface(
                    modifier = Modifier.fillMaxSize(),  // Ocupa toda la pantalla
                    color = MaterialTheme.colorScheme.background  // Color de fondo del tema
                ) {
                    // NAVEGACIÓN: rememberNavController crea el controlador de navegación
                    // remember: Persiste durante recomposiciones
                    val navController = rememberNavController()
                    
                    // VIEWMODEL COMPARTIDO: viewModel() obtiene una instancia compartida
                    // Esta instancia vive mientras la Activity existe
                    // Se comparte entre todas las pantallas (ExerciseSelection, Timer, History)
                    val timerViewModel: TimerViewModel = viewModel()
                    
                    // NavGraph: Define todas las pantallas y rutas de navegación
                    NavGraph(
                        navController = navController,
                        timerViewModel = timerViewModel
                    )
                }
            }
        }
    }
    
    /**
     * onStart() - La Activity es visible pero no está en primer plano
     * 
     * CICLO: onCreate() → onStart() → onResume()
     */
    override fun onStart() {
        super.onStart()
        Log.d("CICLO_VIDA", "onStart")
    }
    
    /**
     * onResume() - La Activity está en primer plano y el usuario puede interactuar
     * 
     * CICLO: onStart() → onResume() → [App activa]
     * 
     * NOTA: El temporizador NO se reanuda automáticamente aquí.
     * El ViewModel mantiene el estado, pero el CountDownTimer se pausó en onStop().
     */
    override fun onResume() {
        super.onResume()
        Log.d("CICLO_VIDA", "onResume — retomando si corresponde")
    }
    
    /**
     * onPause() - La Activity está perdiendo el foco (otra app está apareciendo)
     * 
     * CICLO: [App activa] → onPause() → onStop()
     */
    override fun onPause() {
        super.onPause()
        Log.d("CICLO_VIDA", "onPause")
    }
    
    /**
     * onStop() - La Activity ya no es visible
     * 
     * CICLO: onPause() → onStop() → [App oculta]
     * 
     * IMPORTANTE: Aquí es donde normalmente pausarías el temporizador.
     * En este proyecto, el TimerEngine maneja esto internamente.
     */
    override fun onStop() {
        super.onStop()
        Log.d("CICLO_VIDA", "onStop — temporizador pausado")
    }
    
    /**
     * onDestroy() - La Activity se está destruyendo
     * 
     * CICLO: onStop() → onDestroy() → [Activity destruida]
     * 
     * IMPORTANTE: 
     * - En rotación de pantalla: onCreate() se llama de nuevo, pero ViewModel NO se destruye
     * - En cierre de app: ViewModel se destruye y onCleared() se llama
     */
    override fun onDestroy() {
        super.onDestroy()
        Log.d("CICLO_VIDA", "onDestroy")
    }
    
    /**
     * Inicializa la base de datos con ejercicios predefinidos
     * 
     * CONCEPTOS:
     * 
     * 1. suspend function: Función que puede pausar su ejecución
     *    - Necesaria para operaciones de base de datos (Room)
     *    - Solo puede llamarse desde coroutines
     * 
     * 2. .first(): Obtiene el primer valor de un Flow
     *    - getAllExercises() retorna un Flow<List<Exercise>>
     *    - .first() espera el primer valor y lo retorna
     *    - Útil para operaciones únicas (no observables)
     * 
     * 3. Verificación de existencia: Solo insertamos si la BD está vacía
     *    - Previene duplicados en cada inicio de app
     *    - isNotEmpty() es más eficiente que size > 0
     */
    private suspend fun initializeExercises() {
        // Verificar si ya hay ejercicios en la base de datos
        // .first() obtiene el primer valor del Flow (operación suspend)
        val allExercises = exerciseDao.getAllExercises().first()
        val hasExercises = allExercises.isNotEmpty()
        
        // Solo insertar si la base de datos está vacía
        // Esto previene duplicados si el usuario reinstala la app
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
