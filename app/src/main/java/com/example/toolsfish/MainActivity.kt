package com.example.toolsfish

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.view.WindowCompat
import com.example.toolsfish.navigation.Screen
import com.example.toolsfish.ui.*
import com.example.toolsfish.ui.theme.ToolsFishTheme
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Handle splash screen
        installSplashScreen()

        // Enable edge-to-edge
        WindowCompat.setDecorFitsSystemWindows(window, false)

        setContent {
            ToolsFishTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    ToolsFishApp()
                }
            }
        }
    }

    /**
     * Componente principal que maneja la navegación entre pantallas
     */
    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun ToolsFishApp() {
        var currentScreen by remember { mutableStateOf<Screen>(Screen.MainMenu) }

        Scaffold(
            topBar = {
                if (currentScreen != Screen.MainMenu) {
                    TopAppBar(
                        title = { Text(currentScreen.title) },
                        navigationIcon = {
                            IconButton(onClick = { currentScreen = Screen.MainMenu }) {
                                Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                            }
                        }
                    )
                }
            }
        ) { padding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
            ) {
                when (currentScreen) {
                    is Screen.MainMenu -> MainMenuScreen(
                        onNavigateToCalculator = { currentScreen = Screen.AquariumCalculator },
                        onNavigateToLight = { currentScreen = Screen.LightCalculator }
                    )

                    is Screen.AquariumCalculator -> AquariumCalculatorScreen(onNavigateBack = {
                        currentScreen = Screen.MainMenu
                    })

                    is Screen.LightCalculator -> LightCalculatorScreen(onNavigateBack = {
                        currentScreen = Screen.MainMenu
                    })
                }
            }
        }
    }

    @Preview(showBackground = true)
    @Composable
    fun MainMenuPreview() {
        ToolsFishTheme {
            MainMenuScreen(
                onNavigateToCalculator = { },
                onNavigateToLight = { }
            )
        }
    }

    @Preview(showBackground = true)
    @Composable
    fun AquariumCalculatorPreview() {
        ToolsFishTheme {
            AquariumCalculatorScreen(
                onNavigateBack = { }
            )
        }
    }

    @Preview(showBackground = true)
    @Composable
    fun LightCalculatorPreview() {
        ToolsFishTheme {
            LightCalculatorScreen(
                onNavigateBack = { }
            )
        }
    }
}