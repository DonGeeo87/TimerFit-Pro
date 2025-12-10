package dev.dongeeo.timerfitpro.data.repository

import dev.dongeeo.timerfitpro.data.local.dao.ExerciseDao
import dev.dongeeo.timerfitpro.data.local.entity.ExerciseEntity
import dev.dongeeo.timerfitpro.domain.model.Exercise
import dev.dongeeo.timerfitpro.domain.model.ExerciseCategory
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ExerciseRepository @Inject constructor(
    private val exerciseDao: ExerciseDao
) {
    fun getAllExercises(): Flow<List<Exercise>> {
        return exerciseDao.getAllExercises().map { entities ->
            entities.map { it.toDomain() }
        }
    }
    
    fun getExerciseById(id: String): Flow<Exercise?> {
        return exerciseDao.getAllExercises().map { exercises ->
            exercises.find { it.id == id }?.toDomain()
        }
    }
    
    fun getExercisesByGroup(group: String): Flow<List<Exercise>> {
        return exerciseDao.getExercisesByGroup(group).map { entities ->
            entities.map { it.toDomain() }
        }
    }
    
    fun getExercisesByCategory(category: ExerciseCategory): Flow<List<Exercise>> {
        return exerciseDao.getExercisesByCategory(category.name).map { entities ->
            entities.map { it.toDomain() }
        }
    }
    
    suspend fun insertExercise(exercise: Exercise) {
        exerciseDao.insertExercise(ExerciseEntity.fromDomain(exercise))
    }
    
    suspend fun insertExercises(exercises: List<Exercise>) {
        exerciseDao.insertExercises(exercises.map { ExerciseEntity.fromDomain(it) })
    }
}


