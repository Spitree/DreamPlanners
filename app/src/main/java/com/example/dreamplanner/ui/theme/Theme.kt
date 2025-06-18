package com.example.dreamplanner.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

val accent = Color(0x660FA7E7)
// Define your custom colors
private val LightColorScheme = lightColorScheme(
    primary = Color(0xFFD8B75C),      // ciepły żółty, ale nie jaskrawy
    onPrimary = Color(0xFFB78E2F),    // ciemniejszy, złocisty

    secondary = Color(0xFFC69B3D),    // pomarańczowo-żółty, lekko złoty
    onSecondary = Color(0xFFB4841F),  // ciemniejszy pomarańcz

    background = Color(0xFFF0C75E),   // delikatny, kremowo-żółty
    onBackground = Color(0xFFAA7E2B), // ciemny, złoty odcień

    surface = Color(0xFFE6A951),      // ciepły, stonowany pomarańcz
    onSurface = Color(0xFFBC8742)     // trochę ciemniejszy pomarańcz
)




private val DarkColorScheme = darkColorScheme(
    primary = Color(0xFFBB86FC),
    secondary = Color(0xFF03DAC6),
    background = Color(0xFF121212),
    surface = Color(0xFF121212),
    onPrimary = Color.Black,
    onSecondary = Color.Black,
    onBackground = Color.White,
    onSurface = Color.White
)

@Composable
fun DreamPlannerTheme(
    darkTheme: Boolean = false,
    content: @Composable () -> Unit
) {
    val colors = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colors,
        typography = Typography,
        content = content
    )
}
