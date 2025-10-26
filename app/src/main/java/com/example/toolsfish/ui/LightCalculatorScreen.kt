package com.example.toolsfish.ui

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.toolsfish.models.*
import com.example.toolsfish.sensors.LightSensorData
import com.example.toolsfish.sensors.LightSensorManager
import com.example.toolsfish.ui.theme.*
import kotlinx.coroutines.launch

/**
 * Pantalla principal de la calculadora de iluminación con sensor en tiempo real
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LightCalculatorScreen(
    onNavigateBack: () -> Unit = {}
) {
    val context = LocalContext.current
    val lightSensorManager = remember { LightSensorManager(context) }
    
    var currentIntensity by remember { mutableStateOf(0) }
    var currentColorTemp by remember { mutableStateOf(6500) }
    var lightData by remember { mutableStateOf<LightDisplayData?>(null) }
    var sensorData by remember { mutableStateOf<LightSensorData?>(null) }
    var isSensorActive by remember { mutableStateOf(false) }
    var hasSensor by remember { mutableStateOf(false) }
    var showError by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf("") }
    
    val scope = rememberCoroutineScope()
    
    // Verificar si el dispositivo tiene sensor de luz
    LaunchedEffect(Unit) {
        hasSensor = lightSensorManager.hasLightSensor()
        if (!hasSensor) {
            showError = true
            errorMessage = "Este dispositivo no tiene sensor de luz"
        }
    }
    
    // Calcular datos de iluminación cuando cambien los valores
    LaunchedEffect(currentIntensity, currentColorTemp) {
        val level = LightCalculator.evaluateLightLevel(currentIntensity)
        val isOptimalColor = LightCalculator.isOptimalColorTemperature(currentColorTemp)
        
        lightData = LightDisplayData(
            intensity = currentIntensity,
            colorTemperature = currentColorTemp,
            level = level,
            description = LightCalculator.getLightDescription(level),
            recommendation = LightCalculator.getRecommendation(level),
            isOptimalColor = isOptimalColor,
            colorDescription = LightCalculator.getColorTemperatureDescription(currentColorTemp)
        )
    }
    
    // Manejar el flujo del sensor de luz
    LaunchedEffect(isSensorActive) {
        if (isSensorActive && hasSensor) {
            try {
                lightSensorManager.getLightSensorFlow().collect { data ->
                    sensorData = data
                    currentIntensity = data.intensity
                }
            } catch (e: Exception) {
                showError = true
                errorMessage = "Error al leer el sensor: ${e.message}"
                isSensorActive = false
            }
        }
    }
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        MaterialTheme.colorScheme.background,
                        MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
                    )
                )
            )
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp, vertical = 24.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        // Header con botón de regreso
        ModernLightHeader(
            title = "Calculadora de Iluminación",
            subtitle = "Medición en tiempo real con sensor de luz",
            onNavigateBack = onNavigateBack
        )
        
        // Indicador de estado del sensor
        SensorStatusCard(
            hasSensor = hasSensor,
            isActive = isSensorActive,
            onToggleSensor = { 
                isSensorActive = !isSensorActive
                if (!isSensorActive) {
                    currentIntensity = 0
                    sensorData = null
                }
            }
        )
        
        // Sección de nivel de luz para plantas
        ModernLightLevelCard(lightData = lightData)
        
        // Sección de datos actuales del sensor
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Intensidad del sensor
            ModernCurrentDataCard(
                icon = Icons.Default.Lightbulb,
                label = "Intensidad",
                value = "${currentIntensity} LUX",
                gradientColors = listOf(AquaTeal40, Seaweed40),
                modifier = Modifier.weight(1f)
            )
            
            // Temperatura de color (manual)
            ModernCurrentDataCard(
                icon = Icons.Default.Thermostat,
                label = "Temp. Color",
                value = "${currentColorTemp} K",
                gradientColors = listOf(AquaCyan40, AquaBlue40),
                modifier = Modifier.weight(1f)
            )
        }
        
        // Información del sensor
        sensorData?.let { data ->
            SensorInfoCard(sensorData = data)
        }
        
        // Controles de temperatura de color (manual)
        ModernColorTemperatureControls(
            colorTemp = currentColorTemp,
            onColorTempChange = { currentColorTemp = it }
        )
        
        // Mostrar error si existe
        if (showError) {
            ModernErrorCard(
                errorMessage = errorMessage,
                onDismiss = { showError = false }
            )
        }
    }
}

/**
 * Card que muestra el nivel de luz con indicador visual
 */
@Composable
fun LightLevelCard(lightData: LightDisplayData?) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Nivel de Luz para Plantas",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Indicador visual tipo semáforo
            LightLevelIndicator(lightData?.level)
            
            Spacer(modifier = Modifier.height(12.dp))
            
            // Descripción del nivel
            lightData?.let { data ->
                Text(
                    text = data.description,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium,
                    color = when (data.level) {
                        LightLevel.LOW -> Color(0xFFFF5722)
                        LightLevel.MEDIUM -> Color(0xFFFF9800)
                        LightLevel.HIGH -> Color(0xFF4CAF50)
                        LightLevel.EXCESSIVE -> Color(0xFFF44336)
                    },
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

/**
 * Indicador visual tipo semáforo para el nivel de luz
 */
@Composable
fun LightLevelIndicator(level: LightLevel?) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Luz baja (roja)
        LightIndicatorDot(
            color = if (level == LightLevel.LOW) Color(0xFFFF5722) else Color(0xFFE0E0E0),
            size = 24.dp
        )
        
        // Luz media (amarilla)
        LightIndicatorDot(
            color = if (level == LightLevel.MEDIUM) Color(0xFFFF9800) else Color(0xFFE0E0E0),
            size = 24.dp
        )
        
        // Luz alta (verde)
        LightIndicatorDot(
            color = if (level == LightLevel.HIGH) Color(0xFF4CAF50) else Color(0xFFE0E0E0),
            size = 24.dp
        )
    }
}

/**
 * Punto indicador de luz
 */
@Composable
fun LightIndicatorDot(
    color: Color,
    size: androidx.compose.ui.unit.Dp
) {
    Box(
        modifier = Modifier
            .size(size)
            .clip(CircleShape)
            .background(color)
    )
}

/**
 * Card para mostrar datos actuales
 */
@Composable
fun CurrentDataCard(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    value: String,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(24.dp)
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                text = label,
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onSurface
            )
            
            Spacer(modifier = Modifier.height(4.dp))
            
            Text(
                text = value,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

/**
 * Controles de entrada para la iluminación
 */
@Composable
fun LightInputControls(
    intensity: Int,
    colorTemp: Int,
    onIntensityChange: (Int) -> Unit,
    onColorTempChange: (Int) -> Unit,
    onSaveData: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "Configuración de Luz",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Control de intensidad
            Text(
                text = "Intensidad (LUX): $intensity",
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onSurface
            )
            Slider(
                value = intensity.toFloat(),
                onValueChange = { onIntensityChange(it.toInt()) },
                valueRange = 0f..15000f,
                modifier = Modifier.fillMaxWidth()
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Control de temperatura de color
            Text(
                text = "Temperatura de Color (K): $colorTemp",
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onSurface
            )
            Slider(
                value = colorTemp.toFloat(),
                onValueChange = { onColorTempChange(it.toInt()) },
                valueRange = 2000f..10000f,
                modifier = Modifier.fillMaxWidth()
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Botón para guardar datos
            Button(
                onClick = onSaveData,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Guardar Medición")
            }
        }
    }
}

/**
 * Card para mostrar el historial de datos
 */
@Composable
fun LightHistoryCard(
    historyItems: List<LightHistoryItem>,
    onViewFullHistory: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "Historial de Datos",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            
            Spacer(modifier = Modifier.height(12.dp))
            
            if (historyItems.isEmpty()) {
                Text(
                    text = "No hay datos históricos disponibles",
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(historyItems.take(3)) { item ->
                        HistoryItemRow(item = item)
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            TextButton(
                onClick = onViewFullHistory,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Ver historial completo")
            }
        }
    }
}

/**
 * Fila individual del historial
 */
@Composable
fun HistoryItemRow(item: LightHistoryItem) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = "${item.formattedDate}, ${item.formattedTime}",
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = item.notes.ifEmpty { "Medición de luz" },
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        
        Text(
            text = "${item.intensity} LUX",
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.primary
        )
    }
}

/**
 * Dialog para agregar datos de iluminación
 */
@Composable
fun AddLightDataDialog(
    lightData: LightDisplayData?,
    onDismiss: () -> Unit,
    onSave: (String) -> Unit
) {
    var notes by remember { mutableStateOf("") }
    
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Guardar Medición de Luz") },
        text = {
            Column {
                lightData?.let { data ->
                    Text("Intensidad: ${data.intensity} LUX")
                    Text("Temperatura: ${data.colorTemperature} K")
                    Text("Nivel: ${data.level.displayName}")
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                
                OutlinedTextField(
                    value = notes,
                    onValueChange = { notes = it },
                    label = { Text("Notas (opcional)") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            TextButton(onClick = { onSave(notes) }) {
                Text("Guardar")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar")
            }
        }
    )
}

// ========== FUNCIONES MODERNAS ==========

@Composable
fun ModernLightHeader(
    title: String,
    subtitle: String,
    onNavigateBack: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        // Botón de regreso
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Start
        ) {
            TextButton(
                onClick = onNavigateBack,
                colors = ButtonDefaults.textButtonColors(
                    contentColor = MaterialTheme.colorScheme.primary
                )
            ) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Regresar",
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Menú Principal",
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Título principal
        Text(
            text = title,
            style = MaterialTheme.typography.headlineLarge,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        // Subtítulo
        Text(
            text = subtitle,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f),
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

/**
 * Card que muestra el nivel de luz con indicador visual moderno
 */
@Composable
fun ModernLightLevelCard(lightData: LightDisplayData?) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier.padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(bottom = 16.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.LocalFlorist,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = "Nivel de Luz para Plantas",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
            
            // Indicador visual tipo semáforo
            ModernLightLevelIndicator(lightData?.level)
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Descripción del nivel
            lightData?.let { data ->
                Text(
                    text = data.description,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Medium,
                    color = when (data.level) {
                        LightLevel.LOW -> Color(0xFFFF5722)
                        LightLevel.MEDIUM -> Color(0xFFFF9800)
                        LightLevel.HIGH -> Color(0xFF4CAF50)
                        LightLevel.EXCESSIVE -> Color(0xFFF44336)
                    },
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

/**
 * Indicador visual tipo semáforo moderno para el nivel de luz
 */
@Composable
fun ModernLightLevelIndicator(level: LightLevel?) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Luz baja (roja)
        ModernLightIndicatorDot(
            color = if (level == LightLevel.LOW) Color(0xFFFF5722) else Color(0xFFE0E0E0),
            size = 32.dp,
            isActive = level == LightLevel.LOW
        )
        
        // Luz media (amarilla)
        ModernLightIndicatorDot(
            color = if (level == LightLevel.MEDIUM) Color(0xFFFF9800) else Color(0xFFE0E0E0),
            size = 32.dp,
            isActive = level == LightLevel.MEDIUM
        )
        
        // Luz alta (verde)
        ModernLightIndicatorDot(
            color = if (level == LightLevel.HIGH) Color(0xFF4CAF50) else Color(0xFFE0E0E0),
            size = 32.dp,
            isActive = level == LightLevel.HIGH
        )
        
        // Luz excesiva (roja intensa)
        ModernLightIndicatorDot(
            color = if (level == LightLevel.EXCESSIVE) Color(0xFFF44336) else Color(0xFFE0E0E0),
            size = 32.dp,
            isActive = level == LightLevel.EXCESSIVE
        )
    }
}

/**
 * Punto indicador de luz moderno
 */
@Composable
fun ModernLightIndicatorDot(
    color: Color,
    size: androidx.compose.ui.unit.Dp,
    isActive: Boolean
) {
    Box(
        modifier = Modifier
            .size(size)
            .clip(CircleShape)
            .background(
                brush = if (isActive) {
                    Brush.radialGradient(
                        colors = listOf(color, color.copy(alpha = 0.7f))
                    )
                } else {
                    Brush.radialGradient(
                        colors = listOf(color, color)
                    )
                }
            )
    ) {
        if (isActive) {
            Box(
                modifier = Modifier
                    .size(size * 0.6f)
                    .clip(CircleShape)
                    .background(Color.White.copy(alpha = 0.3f))
            )
        }
    }
}

/**
 * Card para mostrar datos actuales con diseño moderno
 */
@Composable
fun ModernCurrentDataCard(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    value: String,
    gradientColors: List<Color>,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Icono con gradiente
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(
                        brush = Brush.linearGradient(gradientColors)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(24.dp)
                )
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            Text(
                text = label,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            
            Spacer(modifier = Modifier.height(4.dp))
            
            Text(
                text = value,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

/**
 * Controles de entrada para la iluminación con diseño moderno
 */
@Composable
fun ModernLightInputControls(
    intensity: Int,
    colorTemp: Int,
    onIntensityChange: (Int) -> Unit,
    onColorTempChange: (Int) -> Unit,
    onSaveData: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(bottom = 16.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Settings,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = "Configuración de Luz",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
            
            // Control de intensidad
            Text(
                text = "Intensidad (LUX): $intensity",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            Slider(
                value = intensity.toFloat(),
                onValueChange = { onIntensityChange(it.toInt()) },
                valueRange = 0f..15000f,
                modifier = Modifier.fillMaxWidth(),
                colors = SliderDefaults.colors(
                    thumbColor = MaterialTheme.colorScheme.primary,
                    activeTrackColor = MaterialTheme.colorScheme.primary,
                    inactiveTrackColor = MaterialTheme.colorScheme.outline
                )
            )
            
            Spacer(modifier = Modifier.height(20.dp))
            
            // Control de temperatura de color
            Text(
                text = "Temperatura de Color (K): $colorTemp",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            Slider(
                value = colorTemp.toFloat(),
                onValueChange = { onColorTempChange(it.toInt()) },
                valueRange = 2000f..10000f,
                modifier = Modifier.fillMaxWidth(),
                colors = SliderDefaults.colors(
                    thumbColor = MaterialTheme.colorScheme.primary,
                    activeTrackColor = MaterialTheme.colorScheme.primary,
                    inactiveTrackColor = MaterialTheme.colorScheme.outline
                )
            )
            
            Spacer(modifier = Modifier.height(20.dp))
            
            // Botón para guardar datos
            Button(
                onClick = onSaveData,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary
                ),
                shape = RoundedCornerShape(16.dp),
                elevation = ButtonDefaults.buttonElevation(defaultElevation = 4.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Save,
                    contentDescription = null,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Guardar Medición",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}

/**
 * Card para mostrar el historial de datos con diseño moderno
 */
@Composable
fun ModernLightHistoryCard(
    historyItems: List<LightHistoryItem>,
    onViewFullHistory: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(bottom = 16.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.History,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = "Historial de Datos",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
            
            if (historyItems.isEmpty()) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(
                        imageVector = Icons.Default.History,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
                        modifier = Modifier.size(48.dp)
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "No hay datos históricos disponibles",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center
                    )
                }
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(historyItems.take(3)) { item ->
                        ModernHistoryItemRow(item = item)
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            TextButton(
                onClick = onViewFullHistory,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Ver historial completo")
            }
        }
    }
}

/**
 * Fila individual del historial con diseño moderno
 */
@Composable
fun ModernHistoryItemRow(item: LightHistoryItem) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
        ),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = "${item.formattedDate}, ${item.formattedTime}",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = item.notes.ifEmpty { "Medición de luz" },
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            
            Surface(
                modifier = Modifier.clip(RoundedCornerShape(8.dp)),
                color = MaterialTheme.colorScheme.primaryContainer
            ) {
                Text(
                    text = "${item.intensity} LUX",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                )
            }
        }
    }
}

// ========== NUEVAS FUNCIONES PARA SENSOR ==========

/**
 * Card que muestra el estado del sensor de luz
 */
@Composable
fun SensorStatusCard(
    hasSensor: Boolean,
    isActive: Boolean,
    onToggleSensor: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = if (isActive) 
                MaterialTheme.colorScheme.primaryContainer 
            else 
                MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        shape = RoundedCornerShape(16.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = if (hasSensor) Icons.Default.Sensors else Icons.Default.Warning,
                    contentDescription = null,
                    tint = if (hasSensor) {
                        if (isActive) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
                    } else {
                        MaterialTheme.colorScheme.error
                    },
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(
                        text = if (hasSensor) "Sensor de Luz" else "Sensor No Disponible",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = if (hasSensor) {
                            if (isActive) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurface
                        } else {
                            MaterialTheme.colorScheme.onError
                        }
                    )
                    Text(
                        text = if (hasSensor) {
                            if (isActive) "Activo - Leyendo datos" else "Inactivo - Toca para activar"
                        } else {
                            "Este dispositivo no tiene sensor de luz"
                        },
                        style = MaterialTheme.typography.bodySmall,
                        color = if (hasSensor) {
                            if (isActive) MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f) else MaterialTheme.colorScheme.onSurfaceVariant
                        } else {
                            MaterialTheme.colorScheme.onError.copy(alpha = 0.8f)
                        }
                    )
                }
            }
            
            if (hasSensor) {
                Button(
                    onClick = onToggleSensor,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isActive) 
                            MaterialTheme.colorScheme.error 
                        else 
                            MaterialTheme.colorScheme.primary
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(
                        imageVector = if (isActive) Icons.Default.Stop else Icons.Default.PlayArrow,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = if (isActive) "Detener" else "Iniciar",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        }
    }
}

/**
 * Card que muestra información detallada del sensor
 */
@Composable
fun SensorInfoCard(sensorData: LightSensorData) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(bottom = 16.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Info,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = "Información del Sensor",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = "Precisión",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = when (sensorData.accuracy) {
                            3 -> "Alta"
                            2 -> "Media"
                            1 -> "Baja"
                            else -> "Desconocida"
                        },
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
                
                Column {
                    Text(
                        text = "Última lectura",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = "${(System.currentTimeMillis() - sensorData.timestamp) / 1000}s",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        }
    }
}

/**
 * Controles para temperatura de color (manual)
 */
@Composable
fun ModernColorTemperatureControls(
    colorTemp: Int,
    onColorTempChange: (Int) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(bottom = 16.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Palette,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = "Temperatura de Color",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
            
            Text(
                text = "Ajusta manualmente la temperatura de color (K): $colorTemp",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            
            Slider(
                value = colorTemp.toFloat(),
                onValueChange = { onColorTempChange(it.toInt()) },
                valueRange = 2000f..10000f,
                modifier = Modifier.fillMaxWidth(),
                colors = SliderDefaults.colors(
                    thumbColor = MaterialTheme.colorScheme.primary,
                    activeTrackColor = MaterialTheme.colorScheme.primary,
                    inactiveTrackColor = MaterialTheme.colorScheme.outline
                )
            )
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Cálido (2000K)",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = "Neutro (6500K)",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = "Frío (10000K)",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

/**
 * Card de error moderno con botón de cerrar
 */
@Composable
fun ModernErrorCard(
    errorMessage: String,
    onDismiss: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.errorContainer
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        shape = RoundedCornerShape(16.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.Warning,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onErrorContainer,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = errorMessage,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onErrorContainer,
                modifier = Modifier.weight(1f)
            )
            IconButton(
                onClick = onDismiss
            ) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "Cerrar",
                    tint = MaterialTheme.colorScheme.onErrorContainer,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}
