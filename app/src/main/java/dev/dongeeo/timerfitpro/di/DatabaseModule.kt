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

/**
 * DatabaseModule - Módulo de Hilt para proporcionar la base de datos
 * 
 * CONCEPTOS CLAVE DE HILT:
 * 
 * 1. @Module: Marca una clase como módulo de Hilt
 *    - Los módulos proporcionan dependencias que Hilt no puede crear automáticamente
 * 
 * 2. @InstallIn(SingletonComponent::class): Instala el módulo en el componente Singleton
 *    - SingletonComponent: Vive durante toda la vida de la aplicación
 *    - Útil para bases de datos, repositorios, etc.
 * 
 * 3. @Provides: Marca una función como proveedora de una dependencia
 *    - Hilt llama a esta función cuando necesita una instancia de TimerFitDatabase
 *    - Se puede inyectar en cualquier lugar con @Inject
 * 
 * 4. @Singleton: La instancia se crea una sola vez y se reutiliza
 *    - Importante para bases de datos (solo queremos una instancia)
 *    - Ahorra memoria y mejora performance
 * 
 * 5. @ApplicationContext: Inyecta el contexto de la aplicación
 *    - Necesario para crear la base de datos Room
 *    - Diferente del contexto de Activity (vive más tiempo)
 * 
 * FLUJO DE INYECCIÓN:
 * 1. Hilt busca @Inject constructor en TimerViewModel
 * 2. Ve que necesita ExerciseSessionRepository
 * 3. Ve que ExerciseSessionRepository necesita ExerciseSessionDao
 * 4. Llama a provideExerciseSessionDao()
 * 5. Ve que necesita TimerFitDatabase
 * 6. Llama a provideDatabase()
 * 7. Crea todas las instancias y las inyecta
 */
@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {
    /**
     * Proporciona una instancia de TimerFitDatabase
     * 
     * CONCEPTO: Room.databaseBuilder
     * - Crea una instancia de la base de datos Room
     * - Parámetros: contexto, clase de la BD, nombre del archivo
     * - .fallbackToDestructiveMigration(): En desarrollo, recrea la BD si cambia el esquema
     *   (en producción, usarías migraciones reales)
     */
    @Provides
    @Singleton  // Solo una instancia durante toda la vida de la app
    fun provideDatabase(@ApplicationContext context: Context): TimerFitDatabase {
        return Room.databaseBuilder(
            context,                    // Contexto de la aplicación
            TimerFitDatabase::class.java, // Clase de la base de datos
            "timerfit_database"          // Nombre del archivo de la BD
        )
        .fallbackToDestructiveMigration() // ⚠️ Solo desarrollo: recrea BD si cambia esquema
        .build()
    }
    
    /**
     * Proporciona ExerciseDao
     * 
     * CONCEPTO: Dependencia de dependencia
     * - Este método necesita TimerFitDatabase (inyectado automáticamente por Hilt)
     * - Hilt resuelve las dependencias automáticamente
     */
    @Provides
    fun provideExerciseDao(database: TimerFitDatabase): ExerciseDao {
        return database.exerciseDao()
    }
    
    /**
     * Proporciona ExerciseSessionDao
     */
    @Provides
    fun provideExerciseSessionDao(database: TimerFitDatabase): ExerciseSessionDao {
        return database.exerciseSessionDao()
    }
}


