package com.example.toolsfish.models

/**
 * Enum que define los tipos de acuario disponibles
 */
enum class AquariumType(val displayName: String) {
    RECTANGULAR("Rectangular"),
    CYLINDRICAL("Cilíndrico"),
    CORNER("Esquina (Triangular)"),
    HEXAGONAL("Hexagonal")
}

/**
 * Clase de datos para almacenar las medidas del acuario
 */
data class AquariumDimensions(
    val length: Double = 0.0,      // Largo (rectangular) / Diámetro (cilíndrico) / Base (esquina) / Lado (hexagonal)
    val width: Double = 0.0,       // Ancho (rectangular) / Profundidad (esquina)
    val height: Double = 0.0       // Alto (todos los tipos)
)

/**
 * Clase de datos para el resultado del cálculo de volumen
 */
data class VolumeResult(
    val cubicCentimeters: Double,
    val liters: Double,
    val gallons: Double,
    val description: String
)

/**
 * Clase que contiene la lógica de cálculo de volumen para diferentes tipos de acuario
 */
object AquariumVolumeCalculator {
    
    /**
     * Calcula el volumen de un acuario rectangular
     * Fórmula: V = largo × ancho × alto
     */
    private fun calculateRectangularVolume(dimensions: AquariumDimensions): Double {
        return dimensions.length * dimensions.width * dimensions.height
    }
    
    /**
     * Calcula el volumen de un acuario cilíndrico
     * Fórmula: V = π × (radio²) × altura
     */
    private fun calculateCylindricalVolume(dimensions: AquariumDimensions): Double {
        val radius = dimensions.length / 2.0 // El diámetro se convierte en radio
        return Math.PI * radius * radius * dimensions.height
    }
    
    /**
     * Calcula el volumen de un acuario de esquina (triangular)
     * Fórmula: V = (base × profundidad × altura) / 2
     */
    private fun calculateCornerVolume(dimensions: AquariumDimensions): Double {
        return (dimensions.length * dimensions.width * dimensions.height) / 2.0
    }
    
    /**
     * Calcula el volumen de un acuario hexagonal
     * Fórmula: V = 2.598 × lado² × altura
     */
    private fun calculateHexagonalVolume(dimensions: AquariumDimensions): Double {
        return 2.598 * dimensions.length * dimensions.length * dimensions.height
    }
    
    /**
     * Función principal que calcula el volumen según el tipo de acuario
     */
    fun calculateVolume(type: AquariumType, dimensions: AquariumDimensions): VolumeResult {
        val cubicCentimeters = when (type) {
            AquariumType.RECTANGULAR -> calculateRectangularVolume(dimensions)
            AquariumType.CYLINDRICAL -> calculateCylindricalVolume(dimensions)
            AquariumType.CORNER -> calculateCornerVolume(dimensions)
            AquariumType.HEXAGONAL -> calculateHexagonalVolume(dimensions)
        }
        
        val liters = cubicCentimeters / 1000.0
        val gallons = liters * 0.264172
        
        val description = generateDescription(type, dimensions, liters, gallons)
        
        return VolumeResult(
            cubicCentimeters = cubicCentimeters,
            liters = liters,
            gallons = gallons,
            description = description
        )
    }
    
    /**
     * Genera una descripción amigable del resultado
     */
    private fun generateDescription(
        type: AquariumType, 
        dimensions: AquariumDimensions, 
        liters: Double, 
        gallons: Double
    ): String {
        val typeName = when (type) {
            AquariumType.RECTANGULAR -> "rectangular"
            AquariumType.CYLINDRICAL -> "cilíndrico"
            AquariumType.CORNER -> "de esquina"
            AquariumType.HEXAGONAL -> "hexagonal"
        }
        
        val dimensionsText = when (type) {
            AquariumType.RECTANGULAR -> "${dimensions.length.toInt()} cm de largo, ${dimensions.width.toInt()} cm de ancho y ${dimensions.height.toInt()} cm de alto"
            AquariumType.CYLINDRICAL -> "${dimensions.length.toInt()} cm de diámetro y ${dimensions.height.toInt()} cm de alto"
            AquariumType.CORNER -> "${dimensions.length.toInt()} cm de base, ${dimensions.width.toInt()} cm de profundidad y ${dimensions.height.toInt()} cm de alto"
            AquariumType.HEXAGONAL -> "${dimensions.length.toInt()} cm de lado y ${dimensions.height.toInt()} cm de alto"
        }
        
        return "Tu acuario $typeName de $dimensionsText tiene un volumen aproximado de ${String.format("%.1f", liters)} L (${String.format("%.1f", gallons)} galones)."
    }
    
    /**
     * Valida que las dimensiones sean válidas (positivas y no vacías)
     */
    fun validateDimensions(dimensions: AquariumDimensions, type: AquariumType): String? {
        return when {
            dimensions.height <= 0 -> "La altura debe ser mayor a 0"
            dimensions.length <= 0 -> when (type) {
                AquariumType.RECTANGULAR -> "El largo debe ser mayor a 0"
                AquariumType.CYLINDRICAL -> "El diámetro debe ser mayor a 0"
                AquariumType.CORNER -> "La base debe ser mayor a 0"
                AquariumType.HEXAGONAL -> "El lado debe ser mayor a 0"
            }
            dimensions.width <= 0 && (type == AquariumType.RECTANGULAR || type == AquariumType.CORNER) -> 
                if (type == AquariumType.RECTANGULAR) "El ancho debe ser mayor a 0" else "La profundidad debe ser mayor a 0"
            else -> null
        }
    }
}
