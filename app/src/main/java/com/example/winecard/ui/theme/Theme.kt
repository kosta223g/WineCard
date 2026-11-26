package com.example.winecard.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val Light = lightColorScheme(
    primary = Color(0xFF76283B), onPrimary = Color(0xFFFFFFFF),
    primaryContainer = Color(0xFFF4D9DF), onPrimaryContainer = Color(0xFF471322),
    secondary = Color(0xFF70574F), onSecondary = Color(0xFFFFFFFF),
    secondaryContainer = Color(0xFFF0E1D6), onSecondaryContainer = Color(0xFF38251F),
    tertiary = Color(0xFF84591D), onTertiary = Color(0xFFFFFFFF),
    background = Color(0xFFF7F3ED), onBackground = Color(0xFF2B2021),
    surface = Color(0xFFFFFCF8), onSurface = Color(0xFF2B2021),
    surfaceVariant = Color(0xFFEFE4DC), onSurfaceVariant = Color(0xFF655650),
    outline = Color(0xFF85736C), error = Color(0xFFBA1A1A), onError = Color.White
)
private val Dark = darkColorScheme(
    primary = Color(0xFFFFB1C1), onPrimary = Color(0xFF57172A),
    primaryContainer = Color(0xFF76283B), onPrimaryContainer = Color(0xFFFFD9E0),
    secondary = Color(0xFFD8BFB1), onSecondary = Color(0xFF3D2A24),
    secondaryContainer = Color(0xFF49332F), onSecondaryContainer = Color(0xFFF4DED2),
    tertiary = Color(0xFFF0C47D), onTertiary = Color(0xFF442B05),
    background = Color(0xFF191416), onBackground = Color(0xFFF3E8E5),
    surface = Color(0xFF282022), onSurface = Color(0xFFF3E8E5),
    surfaceVariant = Color(0xFF392C2E), onSurfaceVariant = Color(0xFFD0BDB9),
    outline = Color(0xFFA99491), error = Color(0xFFFFB4AB), onError = Color(0xFF690005)
)

@Composable
fun WineCardTheme(darkTheme: Boolean = isSystemInDarkTheme(), content: @Composable () -> Unit) {
    MaterialTheme(colorScheme = if (darkTheme) Dark else Light, typography = Typography, content = content)
}
