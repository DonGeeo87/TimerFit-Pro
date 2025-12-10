package dev.dongeeo.timerfitpro.domain.timer

import android.os.CountDownTimer
import dev.dongeeo.timerfitpro.domain.model.TimerMode
import dev.dongeeo.timerfitpro.domain.model.TimerState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class TimerEngine {
    private var countDownTimer: CountDownTimer? = null
    private val _state = MutableStateFlow(TimerState())
    val state: StateFlow<TimerState> = _state.asStateFlow()
    
    private var workDuration: Long = 0L
    private var restDuration: Long = 0L
    private var totalRounds: Int = 1
    private var currentRound: Int = 0
    private var isWorkPhase: Boolean = true
    private var pausedTimeLeft: Long = 0L
    
    fun startFixedTime(durationMillis: Long) {
        stop()
        _state.value = TimerState(
            timeLeftMillis = durationMillis,
            totalTimeMillis = durationMillis,
            isRunning = true,
            isPaused = false,
            mode = TimerMode.FIXED_TIME
        )
        
        countDownTimer = object : CountDownTimer(durationMillis, 100) {
            override fun onTick(millisUntilFinished: Long) {
                _state.value = _state.value.copy(
                    timeLeftMillis = millisUntilFinished,
                    isRunning = true,
                    isPaused = false
                )
            }
            
            override fun onFinish() {
                _state.value = _state.value.copy(
                    timeLeftMillis = 0L,
                    isRunning = false,
                    isPaused = true
                )
            }
        }.start()
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


