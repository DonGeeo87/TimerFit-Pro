package dev.dongeeo.timerfitpro.domain.model

data class TimerState(
    val timeLeftMillis: Long = 0L,
    val totalTimeMillis: Long = 0L,
    val isRunning: Boolean = false,
    val isPaused: Boolean = true,
    val currentRound: Int = 0,
    val totalRounds: Int = 1,
    val isWorkPhase: Boolean = true,
    val mode: TimerMode = TimerMode.FIXED_TIME
) {
    val progress: Float
        get() = if (totalTimeMillis > 0) {
            (totalTimeMillis - timeLeftMillis).toFloat() / totalTimeMillis.toFloat()
        } else 0f
    
    val progressColor: TimerProgressColor
        get() = when {
            progress > 0.75f -> TimerProgressColor.GREEN
            progress > 0.40f -> TimerProgressColor.YELLOW
            else -> TimerProgressColor.RED
        }
}

enum class TimerProgressColor {
    GREEN,
    YELLOW,
    RED
}


