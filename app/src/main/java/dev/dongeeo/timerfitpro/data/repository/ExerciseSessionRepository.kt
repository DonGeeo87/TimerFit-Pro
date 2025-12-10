package dev.dongeeo.timerfitpro.data.repository

import dev.dongeeo.timerfitpro.data.local.dao.ExerciseSessionDao
import dev.dongeeo.timerfitpro.data.local.entity.ExerciseSessionEntity
import dev.dongeeo.timerfitpro.domain.model.DailySummary
import dev.dongeeo.timerfitpro.domain.model.ExerciseSession
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

/**
 * ExerciseSessionRepository - Repositorio para sesiones de ejercicio
 * 
 * CONCEPTOS CLAVE:
 * 
 * 1. Repository Pattern: Capa de abstracción entre ViewModel y base de datos
 *    - ViewModel no conoce detalles de Room
 *    - Facilita testing (puedes mockear el repositorio)
 *    - Permite cambiar la fuente de datos sin afectar ViewModel
 * 
 * 2. @Singleton: Una sola instancia durante toda la app
 *    - Eficiente en memoria
 *    - Compartido entre ViewModels
 * 
 * 3. @Inject constructor: Hilt inyecta automáticamente el DAO
 *    - No necesitas crear manualmente el repositorio
 *    - Hilt resuelve todas las dependencias
 * 
 * 4. Domain Model vs Entity:
 *    - Entity: Modelo de Room (tiene anotaciones @Entity)
 *    - Domain Model: Modelo puro de Kotlin (sin dependencias de Room)
 *    - fromDomain() / toDomain(): Convierten entre ambos
 *    - Esto mantiene la capa de dominio limpia
 * 
 * 5. Flow: Stream de datos reactivo
 *    - Emite valores cuando cambian los datos
 *    - La UI se actualiza automáticamente
 *    - .map(): Transforma los valores del Flow
 */
@Singleton
class ExerciseSessionRepository @Inject constructor(
    private val sessionDao: ExerciseSessionDao  // Inyectado por Hilt desde DatabaseModule
) {
    /**
     * Inserta una sesión en la base de datos
     * 
     * CONCEPTO: suspend function
     * - Las operaciones de Room son suspend (no bloquean el hilo principal)
     * - Solo se puede llamar desde coroutines
     * - fromDomain(): Convierte el modelo de dominio a entidad de Room
     */
    suspend fun insertSession(session: ExerciseSession) {
        sessionDao.insertSession(ExerciseSessionEntity.fromDomain(session))
    }
    
    /**
     * Obtiene sesiones por fecha
     * 
     * CONCEPTO: Flow y map
     * - sessionDao.getSessionsByDate() retorna Flow<List<ExerciseSessionEntity>>
     * - .map(): Transforma cada lista de entidades a lista de modelos de dominio
     * - toDomain(): Convierte cada entidad a modelo de dominio
     * 
     * RESULTADO: Flow<List<ExerciseSession>>
     * - La UI puede observar este Flow y actualizarse automáticamente
     */
    fun getSessionsByDate(date: String): Flow<List<ExerciseSession>> {
        return sessionDao.getSessionsByDate(date).map { entities ->
            // Transformar lista de entidades a lista de modelos de dominio
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


