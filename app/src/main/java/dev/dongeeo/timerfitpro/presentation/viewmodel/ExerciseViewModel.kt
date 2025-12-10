package dev.dongeeo.timerfitpro.presentation.viewmodel

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.dongeeo.timerfitpro.domain.model.Exercise
import dev.dongeeo.timerfitpro.data.repository.ExerciseRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

@HiltViewModel
class ExerciseViewModel @Inject constructor(
    private val exerciseRepository: ExerciseRepository
) : ViewModel() {
    
    fun getAllExercises(): Flow<List<Exercise>> {
        return exerciseRepository.getAllExercises()
    }
    
    fun getExercisesByGroup(group: String): Flow<List<Exercise>> {
        return exerciseRepository.getExercisesByGroup(group)
    }
}

