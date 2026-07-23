package com.aeromedia.app.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

private val AeroDarkColorScheme = darkColorScheme(
    primary = AeroColors.accentBlue,
    secondary = AeroColors.accentGreen,
    tertiary = AeroColors.skyDuskOrange,
    background = AeroColors.surfaceCharcoal,
    surface = AeroColors.surfaceCharcoalLight,
    onPrimary = AeroColors.textPrimary,
    onSecondary = AeroColors.surfaceCharcoal,
    onBackground = AeroColors.textPrimary,
    onSurface = AeroColors.textPrimary,
)

/**
 * AeroMedia's Compose theme. Always dark — the whole visual language (glossy
 * chrome, brushed metal, leather) is built around a dark base, matching every
 * reference screenshot; there's no light-mode variant to fall back on.
 */
@Composable
fun AeroMediaTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = AeroDarkColorScheme,
        typography = AeroTypography,
        shapes = AeroShapes,
        content = content,
    )
}
