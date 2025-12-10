package dev.dongeeo.timerfitpro.domain.model

data class ExerciseSession(
    val sessionId: Int = 0,
    val exerciseId: String,
    val exerciseName: String,
    val date: String,
    val durationMillis: Long,
    val mode: TimerMode,
    val rounds: Int? = null,
    val workDuration: Long? = null,
    val restDuration: Long? = null
)

enum class TimerMode {
    FIXED_TIME,
    HIIT,
    SERIES,
    COUNT_UP
}


