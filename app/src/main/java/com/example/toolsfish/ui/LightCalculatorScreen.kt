package com.example.toolsfish.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.Thermostat
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.toolsfish.models.*
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

/**
 * Pantalla principal de la calculadora de iluminación
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LightCalculatorScreen(
    onNavigateBack: () -> Unit = {}
) {
    var currentIntensity by remember { mutableStateOf(7500) }
    var currentColorTemp by remember { mutableStateOf(6500) }
    var lightData by remember { mutableStateOf<LightDisplayData?>(null) }
    var historyItems by remember { mutableStateOf<List<LightHistoryItem>>(emptyList()) }
    var showAddDialog by remember { mutableStateOf(false) }
    
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
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Botón de regreso
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Start
        ) {
            TextButton(
                onClick = onNavigateBack
            ) {
                Icon(Icons.Default.ArrowBack, contentDescription = "Regresar")
                Spacer(modifier = Modifier.width(8.dp))
                Text("Menú Principal")
            }
        }
        
        // Título
        Text(
            text = "💡 Calculadora de Iluminación",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.fillMaxWidth()
        )
        
        // Sección de nivel de luz para plantas
        LightLevelCard(lightData = lightData)
        
        // Sección de datos actuales
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Intensidad
            CurrentDataCard(
                icon = Icons.Default.Lightbulb,
                label = "Intensidad",
                value = "${currentIntensity} LUX",
                modifier = Modifier.weight(1f)
            )
            
            // Temperatura de color
            CurrentDataCard(
                icon = Icons.Default.Thermostat,
                label = "Temp. Color",
                value = "${currentColorTemp} K",
                modifier = Modifier.weight(1f)
            )
        }
        
        // Controles de entrada
        LightInputControls(
            intensity = currentIntensity,
            colorTemp = currentColorTemp,
            onIntensityChange = { currentIntensity = it },
            onColorTempChange = { currentColorTemp = it },
            onSaveData = { 
                showAddDialog = true
            }
        )
        
        // Historial de datos
        LightHistoryCard(
            historyItems = historyItems,
            onViewFullHistory = { /* TODO: Implementar vista completa */ }
        )
    }
    
        // Dialog para agregar datos
    if (showAddDialog) {
        AddLightDataDialog(
            lightData = lightData,
            onDismiss = { showAddDialog = false },
            onSave = { notes ->
                // TODO: Implementar guardado en base de datos
                showAddDialog = false
            }
        )
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
