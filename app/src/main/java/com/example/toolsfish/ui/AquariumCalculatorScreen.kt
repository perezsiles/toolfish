package com.example.toolsfish.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.toolsfish.models.*
import com.example.toolsfish.ui.theme.*

/**
 * Pantalla principal de la calculadora de volumen de acuario con diseño moderno
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
    var isCalculated by remember { mutableStateOf(false) } // New state to track if calculation was performed

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
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        // Header con botón de regreso
        ModernHeader(
            title = "Calculadora de Acuario",
            subtitle = "Calcula el volumen y parámetros esenciales",
            onNavigateBack = onNavigateBack
        )

        if (!isCalculated) {
            // Mostrar inputs solo si no se ha calculado
            // Selector de tipo de acuario
            ModernAquariumTypeSelector(
                selectedType = selectedType,
                onTypeSelected = {
                    selectedType = it
                    dimensions = AquariumDimensions() // Resetear dimensiones al cambiar tipo
                    result = null // Limpiar resultado
                }
            )

            // Formulario de medidas dinámico
            ModernDimensionsForm(
                type = selectedType,
                dimensions = dimensions,
                onDimensionsChanged = { dimensions = it }
            )

            // Botón de cálculo moderno
            ModernCalculateButton(
                onClick = {
                    val validationError = AquariumVolumeCalculator.validateDimensions(dimensions, selectedType)
                    if (validationError != null) {
                        errorMessage = validationError
                        showError = true
                    } else {
                        result = AquariumVolumeCalculator.calculateVolume(selectedType, dimensions)
                        isCalculated = true
                        showError = false
                    }
                }
            )
        } else {
            // Mostrar resultado cuando está calculado
            result?.let { volumeResult ->
                ModernVolumeResultCard(volumeResult = volumeResult)
            }

            // Botón para volver a calcular
            Button(
                onClick = {
                    isCalculated = false
                    result = null
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 32.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.secondary
                )
            ) {
                Icon(
                    imageVector = Icons.Default.Refresh,
                    contentDescription = null,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("Volver a Calcular")
            }
        }

        // Mostrar error si existe
        if (showError && errorMessage != null) {
            ModernErrorCard(
                errorMessage = errorMessage!!,
                onDismiss = { showError = false }
            )
        }
    }
}

@Composable
fun ModernHeader(
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
 * Selector de tipo de acuario con diseño moderno
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ModernAquariumTypeSelector(
    selectedType: AquariumType,
    onTypeSelected: (AquariumType) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(bottom = 16.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Category,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = "Tipo de Acuario",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
            
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
                        .menuAnchor(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                        unfocusedBorderColor = MaterialTheme.colorScheme.outline
                    ),
                    shape = RoundedCornerShape(12.dp)
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
 * Formulario dinámico de medidas con diseño moderno
 */
@Composable
fun ModernDimensionsForm(
    type: AquariumType,
    dimensions: AquariumDimensions,
    onDimensionsChanged: (AquariumDimensions) -> Unit
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
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(bottom = 16.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Straighten,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = "Medidas (en centímetros)",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
            
            when (type) {
                AquariumType.RECTANGULAR -> {
                    ModernDimensionField(
                        label = "Largo",
                        value = dimensions.length,
                        onValueChange = { onDimensionsChanged(dimensions.copy(length = it)) },
                        icon = Icons.Default.ArrowForward
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    ModernDimensionField(
                        label = "Ancho",
                        value = dimensions.width,
                        onValueChange = { onDimensionsChanged(dimensions.copy(width = it)) },
                        icon = Icons.Default.ArrowBack
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    ModernDimensionField(
                        label = "Alto",
                        value = dimensions.height,
                        onValueChange = { onDimensionsChanged(dimensions.copy(height = it)) },
                        icon = Icons.Default.ArrowUpward
                    )
                }
                AquariumType.CYLINDRICAL -> {
                    ModernDimensionField(
                        label = "Diámetro",
                        value = dimensions.length,
                        onValueChange = { onDimensionsChanged(dimensions.copy(length = it)) },
                        icon = Icons.Default.RadioButtonUnchecked
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    ModernDimensionField(
                        label = "Alto",
                        value = dimensions.height,
                        onValueChange = { onDimensionsChanged(dimensions.copy(height = it)) },
                        icon = Icons.Default.ArrowUpward
                    )
                }
                AquariumType.CORNER -> {
                    ModernDimensionField(
                        label = "Base",
                        value = dimensions.length,
                        onValueChange = { onDimensionsChanged(dimensions.copy(length = it)) },
                        icon = Icons.Default.CropSquare
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    ModernDimensionField(
                        label = "Profundidad",
                        value = dimensions.width,
                        onValueChange = { onDimensionsChanged(dimensions.copy(width = it)) },
                        icon = Icons.Default.ArrowBack
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    ModernDimensionField(
                        label = "Alto",
                        value = dimensions.height,
                        onValueChange = { onDimensionsChanged(dimensions.copy(height = it)) },
                        icon = Icons.Default.ArrowUpward
                    )
                }
                AquariumType.HEXAGONAL -> {
                    ModernDimensionField(
                        label = "Lado",
                        value = dimensions.length,
                        onValueChange = { onDimensionsChanged(dimensions.copy(length = it)) },
                        icon = Icons.Default.Hexagon
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    ModernDimensionField(
                        label = "Alto",
                        value = dimensions.height,
                        onValueChange = { onDimensionsChanged(dimensions.copy(height = it)) },
                        icon = Icons.Default.ArrowUpward
                    )
                }
            }
        }
    }
}

/**
 * Campo de entrada para dimensiones con diseño moderno
 */
@Composable
fun ModernDimensionField(
    label: String,
    value: Double,
    onValueChange: (Double) -> Unit,
    icon: androidx.compose.ui.graphics.vector.ImageVector
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
        leadingIcon = {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary
            )
        },
        modifier = Modifier.fillMaxWidth(),
        singleLine = true,
        keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(
            keyboardType = androidx.compose.ui.text.input.KeyboardType.Decimal
        ),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = MaterialTheme.colorScheme.primary,
            unfocusedBorderColor = MaterialTheme.colorScheme.outline
        ),
        shape = RoundedCornerShape(12.dp)
    )
}

/**
 * Botón de cálculo con diseño moderno
 */
@Composable
fun ModernCalculateButton(
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
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
            imageVector = Icons.Default.Calculate,
            contentDescription = null,
            modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.width(12.dp))
        Text(
            text = "Calcular Volumen",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold
        )
    }
}

/**
 * Tarjeta que muestra el resultado del cálculo con diseño moderno
 */
@Composable
fun ModernVolumeResultCard(volumeResult: VolumeResult) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
        shape = RoundedCornerShape(20.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Icono de resultado
            Box(
                modifier = Modifier
                    .size(64.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(
                        brush = Brush.radialGradient(
                            colors = listOf(AquaBlue40, AquaCyan40)
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "📊",
                    fontSize = 32.sp
                )
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Text(
                text = "Resultado del Cálculo",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )
            
            Spacer(modifier = Modifier.height(20.dp))
            
            // Volumen en litros
            ResultItem(
                label = "Volumen",
                value = "${String.format("%.1f", volumeResult.liters)} L",
                icon = Icons.Default.WaterDrop
            )
            
            Spacer(modifier = Modifier.height(12.dp))
            
            // Volumen en galones
            ResultItem(
                label = "Equivalente",
                value = "${String.format("%.1f", volumeResult.gallons)} galones",
                icon = Icons.Default.Scale
            )
            
            Spacer(modifier = Modifier.height(20.dp))
            
            // Descripción detallada
            Text(
                text = volumeResult.description,
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f),
                lineHeight = 20.sp
            )
        }
    }
}

@Composable
fun ResultItem(
    label: String,
    value: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth()
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onPrimaryContainer,
            modifier = Modifier.size(20.dp)
        )
        Spacer(modifier = Modifier.width(12.dp))
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f),
            modifier = Modifier.weight(1f)
        )
        Text(
            text = value,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onPrimaryContainer
        )
    }
}

/**
 * Tarjeta de error con diseño moderno
 */
@Composable
fun ModernErrorCard(errorMessage: String) {
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
        }
    }
}