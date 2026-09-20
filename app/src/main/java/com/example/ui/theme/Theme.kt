package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val ManiskColorScheme = darkColorScheme(
    primary = ManiskCyan,
    onPrimary = Color(0xFF042F2E),
    primaryContainer = Color(0xFF164E63),
    onPrimaryContainer = Color(0xFFA5F3FC),
    secondary = ManiskViolet,
    onSecondary = Color(0xFF1E1B4B),
    secondaryContainer = Color(0xFF312E81),
    onSecondaryContainer = Color(0xFFE0E7FF),
    tertiary = ManiskAmber,
    onTertiary = Color(0xFF451A03),
    background = ManiskDarkBg,
    onBackground = ManiskTextPrimary,
    surface = ManiskSurface,
    onSurface = ManiskTextPrimary,
    surfaceVariant = ManiskSurfaceElevated,
    onSurfaceVariant = ManiskTextSecondary,
    outline = ManiskSurfaceBorder,
    error = ManiskCrimson,
    onError = Color.White
)

@Composable
fun ManiskTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = ManiskColorScheme,
        typography = Typography,
        content = content
    )
}

