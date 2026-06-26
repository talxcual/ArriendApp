package com.kleber.arriendapp.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val DarkColorScheme = darkColorScheme(
    primary = Secundario,
    onPrimary = Primario,
    secondary = Secundario,
    onSecondary = OnSecondary,
    background = Primario,
    onBackground = Fondo,
    surface = Primario,
    onSurface = Fondo,
    surfaceVariant = OnSurfaceVariant,
    onSurfaceVariant = Fondo
)

private val LightColorScheme = lightColorScheme(
    primary = Primario,
    onPrimary = OnPrimary,
    secondary = Secundario,
    onSecondary = OnSecondary,
    background = Fondo,
    onBackground = OnSurface,
    surface = Surface,
    onSurface = OnSurface,
    surfaceVariant = SurfaceVariant,
    onSurfaceVariant = OnSurfaceVariant,
    secondaryContainer = Secundario,
    onSecondaryContainer = OnPrimary
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false, // Disable dynamic colors to keep LuxeRental branding pristine
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
