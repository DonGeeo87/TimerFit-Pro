package dev.dongeeo.timerfitpro.domain.model

data class Exercise(
    val id: String,
    val name: String,
    val group: String,
    val iconKey: String,
    val category: ExerciseCategory,
    val description: String = "",
    val muscles: List<String> = emptyList(),
    val difficulty: Difficulty = Difficulty.INTERMEDIATE,
    val equipment: String = "Sin equipo"
)

enum class Difficulty {
    BEGINNER,
    INTERMEDIATE,
    ADVANCED
}

enum class ExerciseCategory {
    MULTIARTICULAR,
    ISOLATION,
    CARDIO,
    CORE,
    FLEXIBILITY
}


