package dev.dongeeo.timerfitpro.presentation.viewmodel

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.dongeeo.timerfitpro.domain.model.DailySummary
import dev.dongeeo.timerfitpro.domain.model.ExerciseSession
import dev.dongeeo.timerfitpro.data.repository.ExerciseSessionRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

@HiltViewModel
class HistoryViewModel @Inject constructor(
    private val sessionRepository: ExerciseSessionRepository
) : ViewModel() {
    
    fun getDailySummaries(): Flow<List<DailySummary>> {
        return sessionRepository.getDailySummaries()
    }
    
    fun getSessionsByDate(date: String): Flow<List<ExerciseSession>> {
        return sessionRepository.getSessionsByDate(date)
    }
    
    fun getAllSessions(): Flow<List<ExerciseSession>> {
        return sessionRepository.getAllSessions()
    }
}

