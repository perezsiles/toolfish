package com.example.toolsfish.models

/**
 * Enum que define los niveles de luz para plantas acuáticas
 */
enum class LightLevel(val displayName: String, val color: String) {
    LOW("Luz Baja", "#FF5722"),      // Rojo - Insuficiente
    MEDIUM("Luz Media", "#FF9800"),  // Naranja - Moderada
    HIGH("Luz Alta", "#4CAF50"),     // Verde - Óptima
    EXCESSIVE("Luz Excesiva", "#F44336") // Rojo - Demasiada luz
}

/**
 * Entidad para almacenar datos de iluminación
 * Temporalmente simplificada sin Room para evitar problemas de compatibilidad
 */
data class LightData(
    val id: Long = 0,
    val intensity: Int,           // Intensidad en LUX
    val colorTemperature: Int,     // Temperatura de color en Kelvin
    val timestamp: Long,          // Timestamp de la medición
    val level: LightLevel,        // Nivel de luz evaluado
    val notes: String = ""        // Notas adicionales
)

/**
 * Clase que contiene la lógica de evaluación de niveles de luz
 */
object LightCalculator {
    
    /**
     * Evalúa el nivel de luz basado en la intensidad (LUX)
     * Criterios para plantas acuáticas:
     * - 0-2000 LUX: Luz baja (insuficiente)
     * - 2000-5000 LUX: Luz media (moderada)
     * - 5000-10000 LUX: Luz alta (óptima)
     * - >10000 LUX: Luz excesiva
     */
    fun evaluateLightLevel(intensity: Int): LightLevel {
        return when {
            intensity < 2000 -> LightLevel.LOW
            intensity < 5000 -> LightLevel.MEDIUM
            intensity < 10000 -> LightLevel.HIGH
            else -> LightLevel.EXCESSIVE
        }
    }
    
    /**
     * Genera una descripción del nivel de luz
     */
    fun getLightDescription(level: LightLevel): String {
        return when (level) {
            LightLevel.LOW -> "Luz Insuficiente - Las plantas pueden tener crecimiento lento"
            LightLevel.MEDIUM -> "Luz Moderada - Buen crecimiento para plantas de bajo requerimiento"
            LightLevel.HIGH -> "Luz Suficiente - Óptima para la mayoría de plantas acuáticas"
            LightLevel.EXCESSIVE -> "Luz Excesiva - Puede causar algas y estrés en plantas"
        }
    }
    
    /**
     * Genera un mensaje de recomendación basado en el nivel
     */
    fun getRecommendation(level: LightLevel): String {
        return when (level) {
            LightLevel.LOW -> "Considera aumentar la intensidad de luz o el tiempo de iluminación"
            LightLevel.MEDIUM -> "Nivel adecuado para plantas de bajo-medio requerimiento"
            LightLevel.HIGH -> "Nivel óptimo para plantas acuáticas"
            LightLevel.EXCESSIVE -> "Reduce la intensidad o tiempo de iluminación para evitar algas"
        }
    }
    
    /**
     * Calcula la temperatura de color recomendada para plantas acuáticas
     * Rango óptimo: 5000K - 7000K (luz blanca a azul-blanca)
     */
    fun isOptimalColorTemperature(temp: Int): Boolean {
        return temp in 5000..7000
    }
    
    /**
     * Genera una descripción de la temperatura de color
     */
    fun getColorTemperatureDescription(temp: Int): String {
        return when {
            temp < 3000 -> "Luz cálida (amarilla) - No ideal para plantas"
            temp < 5000 -> "Luz neutra - Aceptable para plantas"
            temp <= 7000 -> "Luz fría (blanca-azul) - Óptima para plantas acuáticas"
            else -> "Luz muy fría (azul) - Puede ser excesiva"
        }
    }
}

/**
 * Clase de datos para mostrar información de iluminación en la UI
 */
data class LightDisplayData(
    val intensity: Int,
    val colorTemperature: Int,
    val level: LightLevel,
    val description: String,
    val recommendation: String,
    val isOptimalColor: Boolean,
    val colorDescription: String
)

/**
 * Clase para el historial de datos de iluminación
 */
data class LightHistoryItem(
    val id: Long,
    val intensity: Int,
    val colorTemperature: Int,
    val level: LightLevel,
    val timestamp: Long,
    val notes: String,
    val formattedTime: String,
    val formattedDate: String
)
