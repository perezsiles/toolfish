package com.example.toolsfish.repository

import com.example.toolsfish.database.LightDataDao
import com.example.toolsfish.models.LightData
import com.example.toolsfish.models.LightHistoryItem
import java.text.SimpleDateFormat
import java.util.*

/**
 * Repositorio para manejar datos de iluminación
 */
class LightRepository(private val lightDataDao: LightDataDao) {
    
    /**
     * Obtiene todos los datos de iluminación ordenados por fecha
     */
    suspend fun getAllLightData(): List<LightHistoryItem> {
        return lightDataDao.getAllLightData().map { lightData ->
            convertToHistoryItem(lightData)
        }
    }
    
    /**
     * Obtiene los datos recientes de iluminación
     */
    suspend fun getRecentLightData(limit: Int = 10): List<LightHistoryItem> {
        return lightDataDao.getRecentLightData(limit).map { lightData ->
            convertToHistoryItem(lightData)
        }
    }
    
    /**
     * Inserta un nuevo dato de iluminación
     */
    suspend fun insertLightData(lightData: LightData): Long {
        return lightDataDao.insertLightData(lightData)
    }
    
    /**
     * Elimina datos antiguos (más de 30 días)
     */
    suspend fun cleanupOldData() {
        val thirtyDaysAgo = System.currentTimeMillis() - (30 * 24 * 60 * 60 * 1000L)
        lightDataDao.deleteOldData(thirtyDaysAgo)
    }
    
    /**
     * Obtiene el conteo total de datos
     */
    suspend fun getDataCount(): Int {
        return lightDataDao.getDataCount()
    }
    
    /**
     * Convierte LightData a LightHistoryItem para la UI
     */
    private fun convertToHistoryItem(lightData: LightData): LightHistoryItem {
        val date = Date(lightData.timestamp)
        val timeFormat = SimpleDateFormat("h:mm a", Locale.getDefault())
        val dateFormat = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
        
        return LightHistoryItem(
            id = lightData.id,
            intensity = lightData.intensity,
            colorTemperature = lightData.colorTemperature,
            level = lightData.level,
            timestamp = lightData.timestamp,
            notes = lightData.notes,
            formattedTime = timeFormat.format(date),
            formattedDate = dateFormat.format(date)
        )
    }
}
