package com.aeromedia.app.ui.theme

import androidx.compose.ui.graphics.Color

/**
 * Original palette for this project. Sampled *by mood* from the reference
 * screenshots (that late-2000s "Aero" sunrise wallpaper, brushed chrome,
 * saddle-leather + legal-pad notes) rather than picked pixel-for-pixel off
 * any one company's actual app — see README -> "Staying legal on assets".
 */
object AeroColors {

    // --- Sky / background gradient (Home screen, top chrome bars) ---
    val skyDeepNavy = Color(0xFF0B1B33)
    val skyMidBlue = Color(0xFF3E6FBE)
    val skyDuskOrange = Color(0xFFFFB873)
    val skyHorizonGold = Color(0xFFF6D9A0)

    // --- Chrome / brushed metal (buttons, transport bars, panels) ---
    val chromeHighlight = Color(0xFFF3F6FA)
    val chromeMid = Color(0xFF94A7C4)
    val chromeShadow = Color(0xFF3B4C68)
    val chromeDeepShadow = Color(0xFF1A2233)
    val glossWhite = Color(0xCCFFFFFF)

    // --- Accent (selection bar, progress, active states) ---
    val accentGreen = Color(0xFF7CC576)
    val accentGreenDeep = Color(0xFF3E7A3B)
    val accentBlue = Color(0xFF5B9BD5)

    // --- Leather (Notes header) ---
    val leatherLight = Color(0xFF8A5A34)
    val leatherMid = Color(0xFF6B4023)
    val leatherDark = Color(0xFF4A2A16)
    val leatherStitch = Color(0xFFC9A26B)

    // --- Paper (Notes body) ---
    val paperCream = Color(0xFFF7EFC9)
    val paperShadowedCream = Color(0xFFEAE0AF)
    val paperRule = Color(0xFFB9C7DE)
    val paperMargin = Color(0xFFCE7B6E)
    val paperInk = Color(0xFF2B2B2B)

    // --- Neutral surfaces ---
    val surfaceCharcoal = Color(0xFF171C26)
    val surfaceCharcoalLight = Color(0xFF232A38)
    val textPrimary = Color(0xFFEFF3FA)
    val textSecondary = Color(0xFFAEB9CC)
    val divider = Color(0x33FFFFFF)
}
