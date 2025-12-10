package dev.dongeeo.timerfitpro.data.repository

import dev.dongeeo.timerfitpro.data.local.dao.ExerciseSessionDao
import dev.dongeeo.timerfitpro.data.local.entity.ExerciseSessionEntity
import dev.dongeeo.timerfitpro.domain.model.DailySummary
import dev.dongeeo.timerfitpro.domain.model.ExerciseSession
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ExerciseSessionRepository @Inject constructor(
    private val sessionDao: ExerciseSessionDao
) {
    suspend fun insertSession(session: ExerciseSession) {
        sessionDao.insertSession(ExerciseSessionEntity.fromDomain(session))
    }
    
    fun getSessionsByDate(date: String): Flow<List<ExerciseSession>> {
        return sessionDao.getSessionsByDate(date).map { entities ->
            entities.map { it.toDomain() }
        }
    }
    
    fun getDailySummaries(): Flow<List<DailySummary>> {
        return sessionDao.getDailySummaries().map { dataList ->
            dataList.map { data ->
                DailySummary(
                    date = data.date,
                    sessionCount = data.sessionCount,
                    totalDurationMillis = data.totalDurationMillis ?: 0L
                )
            }
        }
    }
    
    fun getAllSessions(): Flow<List<ExerciseSession>> {
        return sessionDao.getAllSessions().map { entities ->
            entities.map { it.toDomain() }
        }
    }
}


