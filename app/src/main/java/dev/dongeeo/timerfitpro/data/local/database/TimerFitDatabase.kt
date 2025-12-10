package dev.dongeeo.timerfitpro.data.local.database

import androidx.room.Database
import androidx.room.RoomDatabase
import dev.dongeeo.timerfitpro.data.local.dao.ExerciseDao
import dev.dongeeo.timerfitpro.data.local.dao.ExerciseSessionDao
import dev.dongeeo.timerfitpro.data.local.entity.ExerciseEntity
import dev.dongeeo.timerfitpro.data.local.entity.ExerciseSessionEntity

@Database(
    entities = [ExerciseEntity::class, ExerciseSessionEntity::class],
    version = 2,
    exportSchema = false
)
abstract class TimerFitDatabase : RoomDatabase() {
    abstract fun exerciseDao(): ExerciseDao
    abstract fun exerciseSessionDao(): ExerciseSessionDao
}


