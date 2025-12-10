package dev.dongeeo.timerfitpro.domain.timer

import android.os.CountDownTimer
import dev.dongeeo.timerfitpro.domain.model.TimerMode
import dev.dongeeo.timerfitpro.domain.model.TimerState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * TimerEngine - Motor del temporizador
 * 
 * CONCEPTOS CLAVE:
 * 
 * 1. Separación de responsabilidades:
 *    - TimerEngine maneja la lógica del temporizador
 *    - ViewModel maneja el estado y la comunicación con la UI
 *    - Esto hace el código más testeable y mantenible
 * 
 * 2. StateFlow:
 *    - MutableStateFlow: Flujo mutable (solo se modifica desde aquí)
 *    - StateFlow: Flujo de solo lectura (se expone a la UI)
 *    - asStateFlow(): Convierte MutableStateFlow a StateFlow (protege el estado)
 * 
 * 3. CountDownTimer:
 *    - Clase de Android para timers
 *    - onTick(): Se llama cada X milisegundos mientras cuenta
 *    - onFinish(): Se llama cuando termina
 *    - start(): Inicia el timer
 *    - cancel(): Detiene el timer
 * 
 * 4. Patrón de encapsulación:
 *    - _state es privado (solo se modifica internamente)
 *    - state es público (solo lectura desde fuera)
 *    - Esto previene modificaciones accidentales del estado
 */
class TimerEngine {
    // CountDownTimer de Android (puede ser null si no hay timer activo)
    private var countDownTimer: CountDownTimer? = null
    
    // Estado mutable (solo se modifica desde esta clase)
    // MutableStateFlow mantiene el último valor emitido
    private val _state = MutableStateFlow(TimerState())
    
    // Estado de solo lectura (se expone a la UI)
    // asStateFlow() convierte el mutable en inmutable
    val state: StateFlow<TimerState> = _state.asStateFlow()
    
    private var workDuration: Long = 0L
    private var restDuration: Long = 0L
    private var totalRounds: Int = 1
    private var currentRound: Int = 0
    private var isWorkPhase: Boolean = true
    private var pausedTimeLeft: Long = 0L
    
    /**
     * Inicia un temporizador de tiempo fijo
     * 
     * CONCEPTOS:
     * 
     * 1. stop(): Siempre detenemos cualquier timer anterior antes de iniciar uno nuevo
     *    Esto previene múltiples timers corriendo simultáneamente
     * 
     * 2. _state.value = ...: Actualizar el estado
     *    - Esto emite el nuevo valor a todos los observadores (UI)
     *    - La UI se actualiza automáticamente gracias a StateFlow
     * 
     * 3. object : CountDownTimer: Clase anónima
     *    - Implementamos CountDownTimer sin crear una clase separada
     *    - Útil cuando solo se usa en un lugar
     * 
     * 4. copy(): Función de data class que crea una copia con algunos valores cambiados
     *    - Útil para actualizar solo algunos campos del estado
     *    - Mantiene inmutabilidad (no modificamos el objeto original)
     * 
     * @param durationMillis Duración total en milisegundos
     */
    fun startFixedTime(durationMillis: Long) {
        // Detener cualquier timer anterior
        stop()
        
        // Inicializar el estado del temporizador
        _state.value = TimerState(
            timeLeftMillis = durationMillis,
            totalTimeMillis = durationMillis,
            isRunning = true,
            isPaused = false,
            mode = TimerMode.FIXED_TIME
        )
        
        // Crear e iniciar el CountDownTimer
        // Parámetros: (duración total, intervalo de actualización)
        // - durationMillis: Cuánto dura el timer
        // - 100: Se actualiza cada 100ms (10 veces por segundo)
        countDownTimer = object : CountDownTimer(durationMillis, 100) {
            // Se llama cada 100ms mientras el timer está corriendo
            override fun onTick(millisUntilFinished: Long) {
                // Actualizar solo los campos que cambian
                // copy() crea una nueva instancia con los valores actualizados
                _state.value = _state.value.copy(
                    timeLeftMillis = millisUntilFinished,  // Tiempo restante
                    isRunning = true,                       // Sigue corriendo
                    isPaused = false                        // No está pausado
                )
            }
            
            // Se llama cuando el timer termina
            override fun onFinish() {
                // Actualizar estado: timer terminado
                _state.value = _state.value.copy(
                    timeLeftMillis = 0L,      // Sin tiempo restante
                    isRunning = false,        // Ya no está corriendo
                    isPaused = true           // Está "pausado" (terminado)
                )
            }
        }.start()  // Iniciar el timer inmediatamente
    }
    
    fun startHIIT(workMillis: Long, restMillis: Long, rounds: Int) {
        stop()
        workDuration = workMillis
        restDuration = restMillis
        totalRounds = rounds
        currentRound = 1
        isWorkPhase = true
        
        _state.value = TimerState(
            timeLeftMillis = workMillis,
            totalTimeMillis = workMillis,
            isRunning = true,
            isPaused = false,
            currentRound = 1,
            totalRounds = rounds,
            isWorkPhase = true,
            mode = TimerMode.HIIT
        )
        
        startHIITRound()
    }
    
    private fun startHIITRound() {
        val currentDuration = if (isWorkPhase) workDuration else restDuration
        
        countDownTimer = object : CountDownTimer(currentDuration, 100) {
            override fun onTick(millisUntilFinished: Long) {
                _state.value = _state.value.copy(
                    timeLeftMillis = millisUntilFinished,
                    totalTimeMillis = currentDuration,
                    isRunning = true,
                    isPaused = false,
                    currentRound = currentRound,
                    isWorkPhase = isWorkPhase
                )
            }
            
            override fun onFinish() {
                if (isWorkPhase) {
                    // Terminó trabajo, iniciar descanso
                    if (currentRound < totalRounds) {
                        isWorkPhase = false
                        startHIITRound()
                    } else {
                        // Terminaron todas las rondas
                        _state.value = _state.value.copy(
                            timeLeftMillis = 0L,
                            isRunning = false,
                            isPaused = true,
                            currentRound = totalRounds
                        )
                    }
                } else {
                    // Terminó descanso, iniciar siguiente ronda de trabajo
                    currentRound++
                    if (currentRound <= totalRounds) {
                        isWorkPhase = true
                        startHIITRound()
                    } else {
                        _state.value = _state.value.copy(
                            timeLeftMillis = 0L,
                            isRunning = false,
                            isPaused = true,
                            currentRound = totalRounds
                        )
                    }
                }
            }
        }.start()
    }
    
    fun startCountUp() {
        stop()
        _state.value = TimerState(
            timeLeftMillis = 0L,
            totalTimeMillis = 0L,
            isRunning = true,
            isPaused = false,
            mode = TimerMode.COUNT_UP
        )
        
        var elapsed = 0L
        countDownTimer = object : CountDownTimer(Long.MAX_VALUE, 100) {
            override fun onTick(millisUntilFinished: Long) {
                elapsed += 100
                _state.value = _state.value.copy(
                    timeLeftMillis = elapsed,
                    totalTimeMillis = elapsed,
                    isRunning = true,
                    isPaused = false
                )
            }
            
            override fun onFinish() {
                // No termina nunca en count up
            }
        }.start()
    }
    
    fun pause() {
        countDownTimer?.cancel()
        pausedTimeLeft = _state.value.timeLeftMillis
        _state.value = _state.value.copy(
            isRunning = false,
            isPaused = true
        )
    }
    
    fun resume() {
        if (_state.value.isPaused && pausedTimeLeft > 0) {
            when (_state.value.mode) {
                TimerMode.FIXED_TIME -> {
                    startFixedTime(pausedTimeLeft)
                }
                TimerMode.HIIT -> {
                    // Reanudar HIIT desde donde quedó
                    val remaining = pausedTimeLeft
                    val currentDuration = if (isWorkPhase) workDuration else restDuration
                    
                    countDownTimer = object : CountDownTimer(remaining, 100) {
                        override fun onTick(millisUntilFinished: Long) {
                            _state.value = _state.value.copy(
                                timeLeftMillis = millisUntilFinished,
                                totalTimeMillis = currentDuration,
                                isRunning = true,
                                isPaused = false
                            )
                        }
                        
                        override fun onFinish() {
                            // Continuar con la lógica de HIIT
                            if (isWorkPhase) {
                                if (currentRound < totalRounds) {
                                    isWorkPhase = false
                                    startHIITRound()
                                } else {
                                    _state.value = _state.value.copy(
                                        timeLeftMillis = 0L,
                                        isRunning = false,
                                        isPaused = true
                                    )
                                }
                            } else {
                                currentRound++
                                if (currentRound <= totalRounds) {
                                    isWorkPhase = true
                                    startHIITRound()
                                } else {
                                    _state.value = _state.value.copy(
                                        timeLeftMillis = 0L,
                                        isRunning = false,
                                        isPaused = true
                                    )
                                }
                            }
                        }
                    }.start()
                }
                TimerMode.COUNT_UP -> {
                    startCountUp()
                }
                TimerMode.SERIES -> {
                    // Similar a fixed time
                    startFixedTime(pausedTimeLeft)
                }
            }
        }
    }
    
    fun stop() {
        countDownTimer?.cancel()
        countDownTimer = null
        _state.value = TimerState()
        pausedTimeLeft = 0L
    }
    
    fun reset() {
        stop()
    }
}


