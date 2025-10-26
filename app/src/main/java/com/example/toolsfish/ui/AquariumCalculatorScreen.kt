package com.example.toolsfish.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.toolsfish.models.*

/**
 * Pantalla principal de la calculadora de volumen de acuario
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AquariumCalculatorScreen(
    onNavigateBack: () -> Unit = {}
) {
    var selectedType by remember { mutableStateOf(AquariumType.RECTANGULAR) }
    var dimensions by remember { mutableStateOf(AquariumDimensions()) }
    var result by remember { mutableStateOf<VolumeResult?>(null) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var showError by remember { mutableStateOf(false) }
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally,
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
                Text(
                    text = "← Menú Principal",
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
        
        // Título
        Text(
            text = "🐠 Calculadora de Volumen de Acuario",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.primary
        )
        
        // Selector de tipo de acuario
        AquariumTypeSelector(
            selectedType = selectedType,
            onTypeSelected = { 
                selectedType = it
                dimensions = AquariumDimensions() // Resetear dimensiones al cambiar tipo
                result = null // Limpiar resultado
            }
        )
        
        // Formulario de medidas dinámico
        DimensionsForm(
            type = selectedType,
            dimensions = dimensions,
            onDimensionsChanged = { dimensions = it }
        )
        
        // Botón de cálculo
        Button(
            onClick = {
                val validationError = AquariumVolumeCalculator.validateDimensions(dimensions, selectedType)
                if (validationError != null) {
                    errorMessage = validationError
                    showError = true
                } else {
                    result = AquariumVolumeCalculator.calculateVolume(selectedType, dimensions)
                    showError = false
                }
            },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary
            )
        ) {
            Text(
                text = "Calcular Volumen",
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium
            )
        }
        
        // Mostrar resultado
        result?.let { volumeResult ->
            VolumeResultCard(volumeResult = volumeResult)
        }
        
        // Mostrar error si existe
        if (showError && errorMessage != null) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.errorContainer
                )
            ) {
                Text(
                    text = errorMessage!!,
                    modifier = Modifier.padding(16.dp),
                    color = MaterialTheme.colorScheme.onErrorContainer,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

/**
 * Selector de tipo de acuario usando ComboBox (ExposedDropdownMenuBox)
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AquariumTypeSelector(
    selectedType: AquariumType,
    onTypeSelected: (AquariumType) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = "Tipo de Acuario",
                fontSize = 18.sp,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface
            )
            
            Spacer(modifier = Modifier.height(12.dp))
            
            ExposedDropdownMenuBox(
                expanded = expanded,
                onExpandedChange = { expanded = !expanded }
            ) {
                OutlinedTextField(
                    value = selectedType.displayName,
                    onValueChange = { },
                    readOnly = true,
                    label = { Text("Selecciona el tipo") },
                    trailingIcon = {
                        ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .menuAnchor()
                )
                
                ExposedDropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false }
                ) {
                    AquariumType.values().forEach { type ->
                        DropdownMenuItem(
                            text = { Text(type.displayName) },
                            onClick = {
                                onTypeSelected(type)
                                expanded = false
                            }
                        )
                    }
                }
            }
        }
    }
}

/**
 * Formulario dinámico de medidas según el tipo de acuario
 */
@Composable
fun DimensionsForm(
    type: AquariumType,
    dimensions: AquariumDimensions,
    onDimensionsChanged: (AquariumDimensions) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = "Medidas (en centímetros)",
                fontSize = 18.sp,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            when (type) {
                AquariumType.RECTANGULAR -> {
                    DimensionField(
                        label = "Largo",
                        value = dimensions.length,
                        onValueChange = { onDimensionsChanged(dimensions.copy(length = it)) }
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    DimensionField(
                        label = "Ancho",
                        value = dimensions.width,
                        onValueChange = { onDimensionsChanged(dimensions.copy(width = it)) }
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    DimensionField(
                        label = "Alto",
                        value = dimensions.height,
                        onValueChange = { onDimensionsChanged(dimensions.copy(height = it)) }
                    )
                }
                AquariumType.CYLINDRICAL -> {
                    DimensionField(
                        label = "Diámetro",
                        value = dimensions.length,
                        onValueChange = { onDimensionsChanged(dimensions.copy(length = it)) }
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    DimensionField(
                        label = "Alto",
                        value = dimensions.height,
                        onValueChange = { onDimensionsChanged(dimensions.copy(height = it)) }
                    )
                }
                AquariumType.CORNER -> {
                    DimensionField(
                        label = "Base",
                        value = dimensions.length,
                        onValueChange = { onDimensionsChanged(dimensions.copy(length = it)) }
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    DimensionField(
                        label = "Profundidad",
                        value = dimensions.width,
                        onValueChange = { onDimensionsChanged(dimensions.copy(width = it)) }
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    DimensionField(
                        label = "Alto",
                        value = dimensions.height,
                        onValueChange = { onDimensionsChanged(dimensions.copy(height = it)) }
                    )
                }
                AquariumType.HEXAGONAL -> {
                    DimensionField(
                        label = "Lado",
                        value = dimensions.length,
                        onValueChange = { onDimensionsChanged(dimensions.copy(length = it)) }
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    DimensionField(
                        label = "Alto",
                        value = dimensions.height,
                        onValueChange = { onDimensionsChanged(dimensions.copy(height = it)) }
                    )
                }
            }
        }
    }
}

/**
 * Campo de entrada para dimensiones
 */
@Composable
fun DimensionField(
    label: String,
    value: Double,
    onValueChange: (Double) -> Unit
) {
    var textValue by remember(value) { mutableStateOf(if (value == 0.0) "" else value.toString()) }
    
    OutlinedTextField(
        value = textValue,
        onValueChange = { newValue ->
            textValue = newValue
            val numericValue = newValue.toDoubleOrNull() ?: 0.0
            onValueChange(numericValue)
        },
        label = { Text(label) },
        modifier = Modifier.fillMaxWidth(),
        singleLine = true,
        keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(
            keyboardType = androidx.compose.ui.text.input.KeyboardType.Decimal
        )
    )
}

/**
 * Tarjeta que muestra el resultado del cálculo
 */
@Composable
fun VolumeResultCard(volumeResult: VolumeResult) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "📊 Resultado",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )
            
            Spacer(modifier = Modifier.height(12.dp))
            
            // Volumen en litros
            Text(
                text = "Volumen: ${String.format("%.1f", volumeResult.liters)} L",
                fontSize = 18.sp,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )
            
            // Volumen en galones
            Text(
                text = "Equivalente: ${String.format("%.1f", volumeResult.gallons)} galones",
                fontSize = 16.sp,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )
            
            Spacer(modifier = Modifier.height(12.dp))
            
            // Descripción detallada
            Text(
                text = volumeResult.description,
                fontSize = 14.sp,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )
        }
    }
}
