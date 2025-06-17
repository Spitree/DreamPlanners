package com.example.dreamplanner.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

val accent = Color(0x660FA7E7)
// Define your custom colors
private val LightColorScheme = lightColorScheme(
    primary = Color(0xFFC9C758),
    onPrimary = Color(0xFFC0AC4A),

    secondary = Color(0xFFE6C44A),
    onSecondary = Color(0xFFCBA228),

    background = Color(0xFFE5AD3E),
    onBackground = Color(0xFFAB7729),

    surface = Color(0xFFF6BD64),
    onSurface = Color(0xFFA97B20),
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
