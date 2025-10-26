package com.example.toolsfish.navigation

/**
 * Sealed class representing all screens in the app
 */
sealed class Screen(val route: String, val title: String) {
    object MainMenu : Screen("main_menu", "Herramientas")
    object AquariumCalculator : Screen("aquarium_calculator", "Calculadora de Acuario")
    object LightCalculator : Screen("light_calculator", "Calculadora de Iluminación")
}
