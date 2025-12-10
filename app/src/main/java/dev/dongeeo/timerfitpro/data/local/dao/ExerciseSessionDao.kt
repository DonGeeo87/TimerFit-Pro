package dev.dongeeo.timerfitpro.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import dev.dongeeo.timerfitpro.data.local.entity.ExerciseSessionEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ExerciseSessionDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSession(session: ExerciseSessionEntity)
    
    @Query("SELECT * FROM exercise_sessions WHERE date = :date ORDER BY sessionId DESC")
    fun getSessionsByDate(date: String): Flow<List<ExerciseSessionEntity>>
    
    @Query("""
        SELECT date, COUNT(*) AS sessionCount, SUM(durationMillis) AS totalDurationMillis
        FROM exercise_sessions
        GROUP BY date
        ORDER BY date DESC
    """)
    fun getDailySummaries(): Flow<List<DailySummaryData>>
    
    @Query("SELECT * FROM exercise_sessions ORDER BY date DESC, sessionId DESC")
    fun getAllSessions(): Flow<List<ExerciseSessionEntity>>
}

data class DailySummaryData(
    val date: String,
    val sessionCount: Int,
    val totalDurationMillis: Long?
)


