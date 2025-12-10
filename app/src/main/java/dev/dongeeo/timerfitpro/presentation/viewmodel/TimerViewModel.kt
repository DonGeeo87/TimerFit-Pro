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

@HiltViewModel
class TimerViewModel @Inject constructor(
    private val sessionRepository: ExerciseSessionRepository
) : ViewModel() {
    
    private val timerEngine = TimerEngine()
    val timerState: StateFlow<TimerState> = timerEngine.state
    
    var selectedExercise: Exercise? = null
        private set
    
    fun selectExercise(exercise: Exercise) {
        selectedExercise = exercise
        Log.d("TIMER_VIEWMODEL", "Ejercicio seleccionado: ${exercise.name}")
    }
    
    
    fun pauseTimer() {
        Log.d("TIMER_VIEWMODEL", "Pausando temporizador")
        timerEngine.pause()
    }
    
    fun resumeTimer() {
        Log.d("TIMER_VIEWMODEL", "Reanudando temporizador")
        timerEngine.resume()
    }
    
    // ID único de la sesión actual (se genera al iniciar el timer)
    private var currentSessionId: String? = null
    private var savedSessionIds = mutableSetOf<String>()
    
    fun saveSession() {
        val state = timerState.value
        
        Log.d("TIMER_VIEWMODEL", "saveSession llamado - timeLeft: ${state.timeLeftMillis}, totalTime: ${state.totalTimeMillis}, isRunning: ${state.isRunning}, mode: ${state.mode}, exercise: ${selectedExercise?.name ?: "null"}, currentSessionId: $currentSessionId")
        
        // Solo guardar si el timer terminó y hay tiempo registrado
        if (state.timeLeftMillis == 0L && 
            !state.isRunning &&
            state.totalTimeMillis > 0 && 
            state.mode != TimerMode.COUNT_UP &&
            currentSessionId != null) {
            
            // Verificar si esta sesión ya fue guardada
            if (savedSessionIds.contains(currentSessionId)) {
                Log.d("TIMER_VIEWMODEL", "⚠️ Sesión ya guardada (ID: $currentSessionId), omitiendo duplicado")
                return
            }
            
            // Usar ejercicio seleccionado o uno genérico
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
            
            val sessionIdToSave = currentSessionId!!
            
            viewModelScope.launch {
                try {
                    sessionRepository.insertSession(session)
                    // Marcar esta sesión como guardada
                    savedSessionIds.add(sessionIdToSave)
                    Log.d("TIMER_VIEWMODEL", "✅ Sesión guardada exitosamente: ${session.exerciseName} - ${formatDuration(session.durationMillis)} - Fecha: ${session.date} - ID: $sessionIdToSave")
                } catch (e: Exception) {
                    Log.e("TIMER_VIEWMODEL", "❌ Error al guardar sesión: ${e.message}", e)
                }
            }
        } else {
            Log.d("TIMER_VIEWMODEL", "⏭️ Sesión no guardada - Condiciones no cumplidas")
        }
    }
    
    private fun generateSessionId(): String {
        val exerciseId = selectedExercise?.id ?: "generic"
        val timestamp = System.currentTimeMillis()
        return "${exerciseId}_${timestamp}"
    }
    
    fun startFixedTime(durationMillis: Long) {
        Log.d("TIMER_VIEWMODEL", "Iniciando temporizador fijo: ${durationMillis}ms")
        // Generar nuevo ID de sesión al iniciar
        currentSessionId = generateSessionId()
        Log.d("TIMER_VIEWMODEL", "Nuevo sessionId generado: $currentSessionId")
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
    
    override fun onCleared() {
        super.onCleared()
        Log.d("TIMER_VIEWMODEL", "ViewModel limpiado")
        timerEngine.stop()
    }
}

