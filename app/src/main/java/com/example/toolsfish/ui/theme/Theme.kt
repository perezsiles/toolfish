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
    background = Color(0xFF0A1A2E), // Azul marino oscuro
    surface = Color(0xFF16213E), // Azul grisáceo
    onPrimary = Color.White,
    onSecondary = Color.White,
    onTertiary = Color.White,
    onBackground = Color(0xFFE8F4FD), // Azul muy claro
    onSurface = Color(0xFFE8F4FD)
)

private val LightColorScheme = lightColorScheme(
    primary = AquaBlue40,
    secondary = AquaTeal40,
    tertiary = AquaCyan40,
    background = Color(0xFFE8F4FD), // Azul muy claro
    surface = Color(0xFFF0F8FF), // Azul blanquecino
    onPrimary = Color.White,
    onSecondary = Color.White,
    onTertiary = Color.White,
    onBackground = Color(0xFF0A1A2E), // Azul marino oscuro
    onSurface = Color(0xFF0A1A2E)
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