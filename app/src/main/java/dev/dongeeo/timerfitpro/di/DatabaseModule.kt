package dev.dongeeo.timerfitpro.di

import android.content.Context
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import dev.dongeeo.timerfitpro.data.local.dao.ExerciseDao
import dev.dongeeo.timerfitpro.data.local.dao.ExerciseSessionDao
import dev.dongeeo.timerfitpro.data.local.database.TimerFitDatabase
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {
    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): TimerFitDatabase {
        return Room.databaseBuilder(
            context,
            TimerFitDatabase::class.java,
            "timerfit_database"
        )
        .fallbackToDestructiveMigration() // Permite recrear la DB si cambia el esquema (solo en desarrollo)
        .build()
    }
    
    @Provides
    fun provideExerciseDao(database: TimerFitDatabase): ExerciseDao {
        return database.exerciseDao()
    }
    
    @Provides
    fun provideExerciseSessionDao(database: TimerFitDatabase): ExerciseSessionDao {
        return database.exerciseSessionDao()
    }
}


