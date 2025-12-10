package dev.dongeeo.timerfitpro.presentation.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.dongeeo.timerfitpro.domain.model.Exercise
import dev.dongeeo.timerfitpro.domain.model.ExerciseSession
import dev.dongeeo.timerfitpro.domain.model.TimerMode
import dev.dongeeo.timerfitpro.domain.model.TimerState
import dev.dongeeo.timerfitpro.domain.timer.TimerEngine
import dev.dongeeo.timerfitpro.data.repository.ExerciseSessionRepository
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject

/**
 * TimerViewModel - ViewModel para gestionar el estado del temporizador
 * 
 * CONCEPTOS CLAVE PARA ESTUDIANTES:
 * 
 * 1. ViewModel: Clase que mantiene el estado de la UI y sobrevive a cambios de configuración
 *    (como rotación de pantalla). No se destruye cuando la Activity se recrea.
 * 
 * 2. @HiltViewModel: Anotación de Hilt (Dependency Injection) que permite inyectar dependencias
 *    en el constructor. Hilt crea automáticamente la instancia del ViewModel.
 * 
 * 3. @Inject constructor: Indica que las dependencias se inyectan automáticamente.
 *    En este caso, sessionRepository se inyecta desde DatabaseModule.
 * 
 * 4. StateFlow: Flujo de datos observable que emite el estado actual y todos los cambios.
 *    La UI (Compose) puede observar este flujo y actualizarse automáticamente.
 * 
 * 5. viewModelScope: Coroutine scope que se cancela automáticamente cuando el ViewModel
 *    se destruye. Útil para operaciones asíncronas (como guardar en base de datos).
 */
@HiltViewModel
class TimerViewModel @Inject constructor(
    private val sessionRepository: ExerciseSessionRepository
) : ViewModel() {
    
    // Motor del temporizador: maneja la lógica de cuenta regresiva
    private val timerEngine = TimerEngine()
    
    // Estado observable del temporizador
    // StateFlow emite el estado actual y todos los cambios posteriores
    // La UI puede observar este flujo con collectAsState() en Compose
    val timerState: StateFlow<TimerState> = timerEngine.state
    
    // Ejercicio seleccionado por el usuario
    // private set: Solo se puede modificar desde dentro de esta clase
    // Esto protege el estado y sigue el principio de encapsulación
    var selectedExercise: Exercise? = null
        private set
    
    /**
     * Selecciona un ejercicio para el temporizador
     * 
     * CONCEPTO: Función pública que permite modificar el estado interno
     * de forma controlada. Esto es parte del patrón MVVM.
     */
    fun selectExercise(exercise: Exercise) {
        selectedExercise = exercise
        Log.d("TIMER_VIEWMODEL", "Ejercicio seleccionado: ${exercise.name}")
    }
    
    /**
     * Pausa el temporizador
     * 
     * CONCEPTO: El ViewModel delega la lógica al TimerEngine.
     * Esto separa responsabilidades: ViewModel maneja estado, Engine maneja lógica.
     */
    fun pauseTimer() {
        Log.d("TIMER_VIEWMODEL", "Pausando temporizador")
        timerEngine.pause()
    }
    
    /**
     * Reanuda el temporizador desde donde se pausó
     */
    fun resumeTimer() {
        Log.d("TIMER_VIEWMODEL", "Reanudando temporizador")
        timerEngine.resume()
    }
    
    // ========== SISTEMA ANTI-DUPLICADOS ==========
    // 
    // PROBLEMA: Si el LaunchedEffect se ejecuta múltiples veces,
    // podríamos guardar la misma sesión varias veces.
    // 
    // SOLUCIÓN: Usamos un ID único por sesión y un Set para rastrear
    // qué sesiones ya fueron guardadas.
    // 
    // CONCEPTO: Set es una colección que no permite duplicados.
    // contains() es O(1) en promedio, muy eficiente.
    private var currentSessionId: String? = null  // ID de la sesión actual
    private var savedSessionIds = mutableSetOf<String>()  // IDs ya guardados
    
    /**
     * Guarda la sesión de ejercicio en la base de datos
     * 
     * CONCEPTOS CLAVE:
     * 
     * 1. Validación de estado: Solo guardamos si el timer terminó correctamente
     * 2. Prevención de duplicados: Verificamos si ya guardamos esta sesión
     * 3. Operación asíncrona: Usamos viewModelScope.launch para no bloquear el hilo principal
     * 4. Manejo de errores: try-catch para capturar errores de base de datos
     * 
     * FLUJO:
     * 1. Verificar condiciones (timer terminado, hay tiempo, etc.)
     * 2. Verificar si ya fue guardado (anti-duplicados)
     * 3. Crear objeto ExerciseSession
     * 4. Guardar en base de datos (asíncrono)
     * 5. Marcar como guardado en el Set
     */
    fun saveSession() {
        // Obtener el estado actual del temporizador
        // .value obtiene el valor actual del StateFlow (no observable, solo lectura)
        val state = timerState.value
        
        Log.d("TIMER_VIEWMODEL", "saveSession llamado - timeLeft: ${state.timeLeftMillis}, totalTime: ${state.totalTimeMillis}, isRunning: ${state.isRunning}, mode: ${state.mode}, exercise: ${selectedExercise?.name ?: "null"}, currentSessionId: $currentSessionId")
        
        // VALIDACIÓN: Solo guardar si se cumplen todas las condiciones
        // - Timer terminó (timeLeftMillis == 0)
        // - No está corriendo (!isRunning)
        // - Hay tiempo registrado (totalTimeMillis > 0)
        // - No es modo COUNT_UP (ese modo no se guarda)
        // - Hay un sessionId (se generó al iniciar)
        if (state.timeLeftMillis == 0L && 
            !state.isRunning &&
            state.totalTimeMillis > 0 && 
            state.mode != TimerMode.COUNT_UP &&
            currentSessionId != null) {
            
            // ANTI-DUPLICADOS: Verificar si esta sesión ya fue guardada
            // contains() es muy eficiente en un Set (O(1) en promedio)
            if (savedSessionIds.contains(currentSessionId)) {
                Log.d("TIMER_VIEWMODEL", "⚠️ Sesión ya guardada (ID: $currentSessionId), omitiendo duplicado")
                return  // Salir de la función sin guardar
            }
            
            // OPERADOR ELVIS (?:): Si selectedExercise es null, usar uno genérico
            // Esto evita NullPointerException y garantiza que siempre tengamos un ejercicio
            val exercise = selectedExercise ?: Exercise(
                id = "generic",
                name = "Ejercicio General",
                group = "General",
                iconKey = "generic",
                category = dev.dongeeo.timerfitpro.domain.model.ExerciseCategory.MULTIARTICULAR
            )
            
            val session = ExerciseSession(
                exerciseId = exercise.id,
                exerciseName = exercise.name,
                date = LocalDate.now().toString(),
                durationMillis = state.totalTimeMillis,
                mode = state.mode,
                rounds = if (state.mode == TimerMode.HIIT) state.totalRounds else null,
                workDuration = if (state.mode == TimerMode.HIIT && state.totalRounds > 0) {
                    // Calcular duración de trabajo basado en rondas
                    state.totalTimeMillis / (state.totalRounds * 2) // Aproximado
                } else null,
                restDuration = if (state.mode == TimerMode.HIIT && state.totalRounds > 0) {
                    state.totalTimeMillis / (state.totalRounds * 2) // Aproximado
                } else null
            )
            
            // Guardar el ID en una variable local porque currentSessionId podría cambiar
            // El operador !! (not-null assertion) es seguro aquí porque ya validamos que no es null
            val sessionIdToSave = currentSessionId!!
            
            // COROUTINES: viewModelScope.launch inicia una coroutine
            // 
            // ¿Por qué usar coroutines?
            // - insertSession() es una función suspend (operación de base de datos)
            // - Las operaciones de BD pueden tardar y no deben bloquear el hilo principal
            // - viewModelScope se cancela automáticamente si el ViewModel se destruye
            // 
            // CONCEPTO: suspend functions solo pueden llamarse desde coroutines
            viewModelScope.launch {
                try {
                    // Guardar en base de datos (operación suspend, puede tardar)
                    sessionRepository.insertSession(session)
                    
                    // Marcar esta sesión como guardada para prevenir duplicados
                    savedSessionIds.add(sessionIdToSave)
                    
                    Log.d("TIMER_VIEWMODEL", "✅ Sesión guardada exitosamente: ${session.exerciseName} - ${formatDuration(session.durationMillis)} - Fecha: ${session.date} - ID: $sessionIdToSave")
                } catch (e: Exception) {
                    // MANEJO DE ERRORES: Capturar cualquier excepción de la base de datos
                    // No queremos que un error de BD crashee la app
                    Log.e("TIMER_VIEWMODEL", "❌ Error al guardar sesión: ${e.message}", e)
                }
            }
        } else {
            Log.d("TIMER_VIEWMODEL", "⏭️ Sesión no guardada - Condiciones no cumplidas")
        }
    }
    
    /**
     * Genera un ID único para identificar una sesión
     * 
     * CONCEPTO: String interpolation en Kotlin
     * "${variable}" inserta el valor de la variable en el string
     * 
     * FORMATO: "exerciseId_timestamp"
     * Ejemplo: "squat_1703123456789"
     */
    private fun generateSessionId(): String {
        val exerciseId = selectedExercise?.id ?: "generic"  // Elvis operator
        val timestamp = System.currentTimeMillis()  // Timestamp en milisegundos
        return "${exerciseId}_${timestamp}"
    }
    
    /**
     * Inicia un temporizador de tiempo fijo
     * 
     * CONCEPTO: Cada vez que iniciamos un timer, generamos un nuevo ID.
     * Esto permite distinguir entre diferentes sesiones del mismo ejercicio.
     * 
     * @param durationMillis Duración en milisegundos (ej: 60000 = 60 segundos)
     */
    fun startFixedTime(durationMillis: Long) {
        Log.d("TIMER_VIEWMODEL", "Iniciando temporizador fijo: ${durationMillis}ms")
        
        // Generar nuevo ID de sesión al iniciar
        // Esto permite rastrear sesiones individuales y prevenir duplicados
        currentSessionId = generateSessionId()
        Log.d("TIMER_VIEWMODEL", "Nuevo sessionId generado: $currentSessionId")
        
        // Delegar al TimerEngine que maneja la lógica del temporizador
        timerEngine.startFixedTime(durationMillis)
    }
    
    fun startHIIT(workMillis: Long, restMillis: Long, rounds: Int) {
        Log.d("TIMER_VIEWMODEL", "Iniciando HIIT: trabajo=${workMillis}ms, descanso=${restMillis}ms, rondas=$rounds")
        // Generar nuevo ID de sesión al iniciar
        currentSessionId = generateSessionId()
        Log.d("TIMER_VIEWMODEL", "Nuevo sessionId generado: $currentSessionId")
        timerEngine.startHIIT(workMillis, restMillis, rounds)
    }
    
    fun startCountUp() {
        Log.d("TIMER_VIEWMODEL", "Iniciando conteo ascendente")
        // Generar nuevo ID de sesión al iniciar
        currentSessionId = generateSessionId()
        Log.d("TIMER_VIEWMODEL", "Nuevo sessionId generado: $currentSessionId")
        timerEngine.startCountUp()
    }
    
    fun stopTimer() {
        Log.d("TIMER_VIEWMODEL", "Deteniendo temporizador")
        // Limpiar sessionId al detener
        currentSessionId = null
        timerEngine.stop()
    }
    
    private fun formatDuration(millis: Long): String {
        val totalSeconds = millis / 1000
        val minutes = totalSeconds / 60
        val seconds = totalSeconds % 60
        return if (minutes > 0) {
            "${minutes}m ${seconds}s"
        } else {
            "${seconds}s"
        }
    }
    
    /**
     * Llamado cuando el ViewModel se destruye
     * 
     * CONCEPTO: Ciclo de vida del ViewModel
     * - Se llama cuando la Activity/Fragment se destruye permanentemente
     * - Útil para limpiar recursos (cancelar coroutines, detener timers, etc.)
     * - viewModelScope se cancela automáticamente aquí
     * 
     * IMPORTANTE: Esto NO se llama en rotación de pantalla, solo en destrucción permanente
     */
    override fun onCleared() {
        super.onCleared()
        Log.d("TIMER_VIEWMODEL", "ViewModel limpiado")
        
        // Detener el timer para evitar memory leaks
        timerEngine.stop()
    }
}

