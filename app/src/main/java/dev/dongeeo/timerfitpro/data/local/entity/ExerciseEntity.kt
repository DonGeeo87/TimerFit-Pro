package dev.dongeeo.timerfitpro.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import dev.dongeeo.timerfitpro.domain.model.Exercise
import dev.dongeeo.timerfitpro.domain.model.ExerciseCategory

@Entity(tableName = "exercises")
data class ExerciseEntity(
    @PrimaryKey val id: String,
    val name: String,
    val group: String,
    val iconKey: String,
    val category: String,
    val description: String = "",
    val muscles: String = "", // JSON array como string
    val difficulty: String = "INTERMEDIATE",
    val equipment: String = "Sin equipo"
) {
    fun toDomain(): Exercise {
        val musclesList = if (muscles.isNotEmpty()) {
            muscles.split(",").map { it.trim() }
        } else {
            emptyList()
        }
        
        return Exercise(
            id = id,
            name = name,
            group = group,
            iconKey = iconKey,
            category = ExerciseCategory.valueOf(category),
            description = description,
            muscles = musclesList,
            difficulty = dev.dongeeo.timerfitpro.domain.model.Difficulty.valueOf(difficulty),
            equipment = equipment
        )
    }
    
    companion object {
        fun fromDomain(exercise: Exercise): ExerciseEntity {
            return ExerciseEntity(
                id = exercise.id,
                name = exercise.name,
                group = exercise.group,
                iconKey = exercise.iconKey,
                category = exercise.category.name,
                description = exercise.description,
                muscles = exercise.muscles.joinToString(","),
                difficulty = exercise.difficulty.name,
                equipment = exercise.equipment
            )
        }
    }
}


