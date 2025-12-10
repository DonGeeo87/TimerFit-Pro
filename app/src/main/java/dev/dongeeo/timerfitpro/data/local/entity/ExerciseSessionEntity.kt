package dev.dongeeo.timerfitpro.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import dev.dongeeo.timerfitpro.domain.model.ExerciseSession
import dev.dongeeo.timerfitpro.domain.model.TimerMode

@Entity(tableName = "exercise_sessions")
data class ExerciseSessionEntity(
    @PrimaryKey(autoGenerate = true) val sessionId: Int = 0,
    val exerciseId: String,
    val exerciseName: String,
    val date: String,
    val durationMillis: Long,
    val mode: String,
    val rounds: Int? = null,
    val workDuration: Long? = null,
    val restDuration: Long? = null
) {
    fun toDomain(): ExerciseSession {
        return ExerciseSession(
            sessionId = sessionId,
            exerciseId = exerciseId,
            exerciseName = exerciseName,
            date = date,
            durationMillis = durationMillis,
            mode = TimerMode.valueOf(mode),
            rounds = rounds,
            workDuration = workDuration,
            restDuration = restDuration
        )
    }
    
    companion object {
        fun fromDomain(session: ExerciseSession): ExerciseSessionEntity {
            return ExerciseSessionEntity(
                sessionId = session.sessionId,
                exerciseId = session.exerciseId,
                exerciseName = session.exerciseName,
                date = session.date,
                durationMillis = session.durationMillis,
                mode = session.mode.name,
                rounds = session.rounds,
                workDuration = session.workDuration,
                restDuration = session.restDuration
            )
        }
    }
}


