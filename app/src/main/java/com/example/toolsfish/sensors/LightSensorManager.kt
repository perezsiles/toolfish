package com.example.toolsfish.sensors

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.launch

/**
 * Manager para el sensor de luz del dispositivo Android
 */
class LightSensorManager(private val context: Context) {
    
    private val sensorManager: SensorManager by lazy {
        context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
    }
    
    private val lightSensor: Sensor? by lazy {
        sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT)
    }
    
    /**
     * Verifica si el dispositivo tiene sensor de luz
     */
    fun hasLightSensor(): Boolean {
        return lightSensor != null
    }
    
    /**
     * Obtiene el flujo de datos del sensor de luz
     */
    fun getLightSensorFlow(): Flow<LightSensorData> = callbackFlow {
        if (!hasLightSensor()) {
            close()
            return@callbackFlow
        }
        
        val listener = object : SensorEventListener {
            override fun onSensorChanged(event: SensorEvent?) {
                event?.let { sensorEvent ->
                    if (sensorEvent.sensor.type == Sensor.TYPE_LIGHT) {
                        val lightValue = sensorEvent.values[0]
                        val sensorData = LightSensorData(
                            intensity = lightValue.toInt(),
                            timestamp = System.currentTimeMillis(),
                            accuracy = sensorEvent.accuracy
                        )
                        trySend(sensorData)
                    }
                }
            }
            
            override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
                // Manejar cambios en la precisión del sensor si es necesario
            }
        }
        
        // Registrar el listener
        sensorManager.registerListener(
            listener,
            lightSensor,
            SensorManager.SENSOR_DELAY_NORMAL
        )
        
        // Limpiar cuando se cancele el flujo
        awaitClose {
            sensorManager.unregisterListener(listener)
        }
    }
    
    /**
     * Obtiene una lectura única del sensor de luz
     */
    suspend fun getCurrentLightReading(): LightSensorData? {
        if (!hasLightSensor()) return null
        
        return callbackFlow {
            val listener = object : SensorEventListener {
                override fun onSensorChanged(event: SensorEvent?) {
                    event?.let { sensorEvent ->
                        if (sensorEvent.sensor.type == Sensor.TYPE_LIGHT) {
                            val lightValue = sensorEvent.values[0]
                            val sensorData = LightSensorData(
                                intensity = lightValue.toInt(),
                                timestamp = System.currentTimeMillis(),
                                accuracy = sensorEvent.accuracy
                            )
                            trySend(sensorData)
                            close()
                        }
                    }
                }
                
                override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
                    // No necesario para lectura única
                }
            }
            
            sensorManager.registerListener(
                listener,
                lightSensor,
                SensorManager.SENSOR_DELAY_NORMAL
            )
            
            awaitClose {
                sensorManager.unregisterListener(listener)
            }
        }.let { flow ->
            var result: LightSensorData? = null
            flow.collect { data ->
                result = data
                return@collect // Solo tomar el primer valor
            }
            result
        }
    }
}

/**
 * Clase de datos para la información del sensor de luz
 */
data class LightSensorData(
    val intensity: Int, // Intensidad en LUX
    val timestamp: Long, // Timestamp de la lectura
    val accuracy: Int // Precisión del sensor
)
