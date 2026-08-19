package com.pjcalc.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

private val PjColorScheme = darkColorScheme(
    primary = PjAccent,
    onPrimary = PjBackground,
    secondary = PjSurfaceAlt,
    onSecondary = PjTextPrimary,
    background = PjBackground,
    onBackground = PjTextPrimary,
    surface = PjSurface,
    onSurface = PjTextPrimary,
    surfaceVariant = PjSurfaceAlt,
    onSurfaceVariant = PjTextSecondary,
    outline = PjBorder,
    error = PjError,
    onError = PjBackground
)

@Composable
fun CalculadoraPjTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = PjColorScheme,
        typography = PjTypography,
        content = content
    )
}
