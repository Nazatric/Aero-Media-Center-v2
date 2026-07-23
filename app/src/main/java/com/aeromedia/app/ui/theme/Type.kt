package com.aeromedia.app.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

/**
 * Font: the real Segoe UI isn't freely redistributable, so this uses the
 * platform default for now. [Selawik](https://github.com/microsoft/Selawik)
 * — Microsoft's own SIL Open Font License, metric-compatible Segoe
 * substitute — is the right long-term choice for that exact "Aero era"
 * type rhythm, but its .ttf files need a real download, and this project
 * was scaffolded in a sandbox with no network access.
 *
 * To wire it in once you've downloaded the four Selawik weights into
 * res/font/: replace `FontFamily.Default` below with
 * `FontFamily(Font(R.font.selawik_regular), Font(R.font.selawik_semibold, FontWeight.SemiBold), ...)`.
 * Nothing else in the app needs to change — every text style in the app
 * references `AeroTypography`, not a hardcoded family.
 */
private val AeroFontFamily = FontFamily.Default

val AeroTypography = Typography(
    displayLarge = TextStyle(
        fontFamily = AeroFontFamily,
        fontWeight = FontWeight.Light,
        fontSize = 57.sp,
    ),
    headlineLarge = TextStyle(
        fontFamily = AeroFontFamily,
        fontWeight = FontWeight.SemiBold,
        fontSize = 28.sp,
    ),
    headlineMedium = TextStyle(
        fontFamily = AeroFontFamily,
        fontWeight = FontWeight.SemiBold,
        fontSize = 22.sp,
    ),
    titleLarge = TextStyle(
        fontFamily = AeroFontFamily,
        fontWeight = FontWeight.Medium,
        fontSize = 20.sp,
        letterSpacing = 0.5.sp,
    ),
    titleMedium = TextStyle(
        fontFamily = AeroFontFamily,
        fontWeight = FontWeight.Medium,
        fontSize = 16.sp,
    ),
    bodyLarge = TextStyle(
        fontFamily = AeroFontFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
    ),
    bodyMedium = TextStyle(
        fontFamily = AeroFontFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 14.sp,
    ),
    labelLarge = TextStyle(
        fontFamily = AeroFontFamily,
        fontWeight = FontWeight.Medium,
        fontSize = 14.sp,
    ),
    labelSmall = TextStyle(
        fontFamily = AeroFontFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 11.sp,
    ),
)
