package com.example.toolsfish.database

import com.example.toolsfish.models.LightData

/**
 * DAO para operaciones de base de datos de datos de iluminación
 * Temporalmente simplificado sin Room para evitar problemas de compatibilidad
 */
interface LightDataDao {
    suspend fun getAllLightData(): List<LightData>
    suspend fun getRecentLightData(limit: Int): List<LightData>
    suspend fun insertLightData(lightData: LightData): Long
    suspend fun deleteOldData(cutoffTime: Long)
    suspend fun getDataCount(): Int
}

/**
 * Base de datos simplificada para datos de iluminación
 * Temporalmente sin Room para evitar problemas de compatibilidad
 */
class LightDatabase {
    fun lightDataDao(): LightDataDao {
        return object : LightDataDao {
            override suspend fun getAllLightData(): List<LightData> = emptyList()
            override suspend fun getRecentLightData(limit: Int): List<LightData> = emptyList()
            override suspend fun insertLightData(lightData: LightData): Long = 0L
            override suspend fun deleteOldData(cutoffTime: Long) {}
            override suspend fun getDataCount(): Int = 0
        }
    }
}
