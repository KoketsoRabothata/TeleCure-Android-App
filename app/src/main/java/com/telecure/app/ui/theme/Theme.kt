package com.telecure.app.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val LightColors = lightColorScheme(
    primary = TealPrimary,
    onPrimary = OffWhite,
    secondary = BlueAccent,
    background = OffWhite,
    surface = OffWhite,
    error = ErrorRed,
    onBackground = TextPrimary,
    onSurface = TextPrimary
)

private val DarkColors = darkColorScheme(
    primary = TealLight,
    onPrimary = TextPrimary,
    secondary = BlueAccent,
    background = DarkBackground,
    surface = DarkBackground
)

@Composable
fun TeleCureTheme(darkTheme: Boolean = isSystemInDarkTheme(), content: @Composable () -> Unit) {
    val colors = if (darkTheme) DarkColors else LightColors
    MaterialTheme(colorScheme = colors, typography = TeleCureTypography, content = content)
}
