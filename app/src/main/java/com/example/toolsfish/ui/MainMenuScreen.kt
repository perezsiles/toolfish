package com.example.toolsfish.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainMenuScreen(
    onNavigateToCalculator: () -> Unit,
    onNavigateToLight: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        Text(
            text = "Herramientas",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(bottom = 16.dp)
        )
        
        // Calculator Card
        ToolCard(
            title = "Calculadora de Acuario",
            description = "Calcula el volumen de agua y otros parámetros",
            onClick = onNavigateToCalculator
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Light Calculator Card
        ToolCard(
            title = "Calculadora de Iluminación",
            description = "Calcula la iluminación necesaria para tu acuario",
            onClick = onNavigateToLight
        )
        
        // Add more tools here as needed
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ToolCard(
    title: String,
    description: String,
    onClick: () -> Unit,
    enabled: Boolean = true
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        onClick = if (enabled) onClick else { ->},
        colors = CardDefaults.cardColors(
            containerColor = if (enabled) 
                MaterialTheme.colorScheme.surface 
            else 
                MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = title,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = if (enabled) 
                        MaterialTheme.colorScheme.onSurface 
                    else 
                        MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = description,
                    fontSize = 14.sp,
                    color = if (enabled) 
                        MaterialTheme.colorScheme.onSurfaceVariant 
                    else 
                        MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            
            if (enabled) {
                Text(
                    text = "→",
                    fontSize = 20.sp,
                    color = MaterialTheme.colorScheme.primary
                )
            } else {
                Text(
                    text = "Próximamente",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}