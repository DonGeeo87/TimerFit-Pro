package dev.dongeeo.timerfitpro.data.local.database

import androidx.room.Database
import androidx.room.RoomDatabase
import dev.dongeeo.timerfitpro.data.local.dao.ExerciseDao
import dev.dongeeo.timerfitpro.data.local.dao.ExerciseSessionDao
import dev.dongeeo.timerfitpro.data.local.entity.ExerciseEntity
import dev.dongeeo.timerfitpro.data.local.entity.ExerciseSessionEntity

/**
 * TimerFitDatabase - Base de datos Room
 * 
 * CONCEPTOS CLAVE DE ROOM:
 * 
 * 1. @Database: Anotación que marca una clase como base de datos Room
 *    - entities: Lista de todas las entidades (tablas) en la base de datos
 *    - version: Versión del esquema (incrementar cuando cambias la estructura)
 *    - exportSchema: Si exportar el esquema a JSON (útil para migraciones)
 * 
 * 2. RoomDatabase: Clase abstracta base para todas las bases de datos Room
 *    - Room genera la implementación automáticamente
 *    - Proporciona métodos para obtener DAOs
 * 
 * 3. DAO (Data Access Object): Interfaz que define operaciones de base de datos
 *    - Cada entidad tiene su DAO
 *    - Room genera la implementación automáticamente
 * 
 * 4. version = 2: Si cambias las entidades, debes incrementar la versión
 *    - Room usa esto para detectar cambios en el esquema
 *    - Necesitas migraciones para actualizar datos existentes
 * 
 * FLUJO:
 * Database → DAO → Repository → ViewModel → UI
 */
@Database(
    entities = [ExerciseEntity::class, ExerciseSessionEntity::class],  // Tablas de la BD
    version = 2,  // Versión del esquema (incrementar si cambias las entidades)
    exportSchema = false  // No exportar esquema (solo desarrollo)
)
abstract class TimerFitDatabase : RoomDatabase() {
    // Métodos abstractos que Room implementa automáticamente
    // Cada método retorna un DAO para acceder a una tabla específica
    abstract fun exerciseDao(): ExerciseDao
    abstract fun exerciseSessionDao(): ExerciseSessionDao
}


