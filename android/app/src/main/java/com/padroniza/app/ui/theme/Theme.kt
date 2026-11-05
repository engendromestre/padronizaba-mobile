package com.padroniza.app.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val LightColorScheme = lightColorScheme(
    primary = PadronizaRed,
    onPrimary = SurfaceLight,
    primaryContainer = PadronizaRedLight,
    surface = SurfaceLight,
    onSurface = TextPrimary,
    background = BackgroundLight,
    onBackground = TextPrimary,
    outline = BorderLight
)

@Composable
fun PadronizaTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = LightColorScheme,
        content = content
    )
}
