package com.example.toolsfish.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme = darkColorScheme(
    primary = AquaBlue80,
    secondary = AquaTeal80,
    tertiary = AquaCyan80,
    background = Color(0xFF0A1A2E), // Azul marino profundo
    surface = Color(0xFF16213E), // Azul grisáceo
    surfaceVariant = Color(0xFF1E2A3A), // Variante de superficie
    primaryContainer = Color(0xFF1A237E), // Contenedor primario
    secondaryContainer = Color(0xFF004D40), // Contenedor secundario
    onPrimary = Color.White,
    onSecondary = Color.White,
    onTertiary = Color.White,
    onBackground = Color(0xFFE8F4FD), // Azul muy claro
    onSurface = Color(0xFFE8F4FD),
    onSurfaceVariant = Color(0xFFB0BEC5), // Texto en superficie variante
    onPrimaryContainer = Color(0xFFE1F5FE), // Texto en contenedor primario
    onSecondaryContainer = Color(0xFFE0F2F1) // Texto en contenedor secundario
)

private val LightColorScheme = lightColorScheme(
    primary = AquaBlue40,
    secondary = AquaTeal40,
    tertiary = AquaCyan40,
    background = Color(0xFFF8FAFF), // Blanco azulado muy suave
    surface = Color(0xFFFFFFFF), // Blanco puro
    surfaceVariant = Color(0xFFE3F2FD), // Variante de superficie
    primaryContainer = Color(0xFFE3F2FD), // Contenedor primario
    secondaryContainer = Color(0xFFE0F2F1), // Contenedor secundario
    onPrimary = Color.White,
    onSecondary = Color.White,
    onTertiary = Color.White,
    onBackground = Color(0xFF0A1A2E), // Azul marino oscuro
    onSurface = Color(0xFF0A1A2E),
    onSurfaceVariant = Color(0xFF37474F), // Texto en superficie variante
    onPrimaryContainer = Color(0xFF0D47A1), // Texto en contenedor primario
    onSecondaryContainer = Color(0xFF004D40) // Texto en contenedor secundario
)

@Composable
fun ToolsFishTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color is available on Android 12+
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }

        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}