package com.aeromedia.app.ui.theme

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * The skeuomorphic design system for AeroMedia: original Compose modifiers
 * that fake real-world material (brushed metal, glossy plastic/chrome,
 * leather, paper) using layered gradients and shadows — the same technique
 * every skeuomorphic UI from the 2007-2012 era used, just written from
 * scratch for Compose rather than copied from any one app's assets.
 *
 * The recipe behind all of these is the same one real skeuomorphic CSS/UIKit
 * work has always used: a light-to-dark gradient for the base curvature, a
 * soft highlight near the "light source" edge, and a drop shadow to lift the
 * element off the surface beneath it. Buttons additionally get a *press*
 * state that inverts/flattens the gradient, like a real button being pushed in.
 */

/** Sky gradient used behind the Home screen and Start-style chrome — a
 *  diagonal dusk/sunrise sweep (deep navy through blue and orange to gold)
 *  plus a soft glow near the upper-right to stand in for the reference's
 *  light-ray highlight, built from gradients rather than any specific photo. */
fun Modifier.aeroSkyBackground(): Modifier = this
    .background(
        Brush.linearGradient(
            colorStops = arrayOf(
                0.0f to AeroColors.skyDeepNavy,
                0.32f to AeroColors.skyMidBlue,
                0.58f to Color(0xFF8C6A8F),
                0.78f to AeroColors.skyDuskOrange,
                1.0f to AeroColors.skyHorizonGold,
            ),
            start = Offset(0f, 0f),
            end = Offset(Float.POSITIVE_INFINITY, Float.POSITIVE_INFINITY),
        ),
    )
    .drawWithContent {
        drawContent()
        // Soft light-source glow, upper-right — the diagonal "sunbeam" the
        // reference wallpaper has, approximated with a radial highlight
        // rather than reproducing that specific photo.
        drawRect(
            brush = Brush.radialGradient(
                colors = listOf(Color.White.copy(alpha = 0.16f), Color.Transparent),
                center = Offset(size.width * 0.82f, size.height * 0.06f),
                radius = size.width * 0.9f,
            ),
        )
    }

/** Vertical brushed-metal panel, used for chrome bars (transport bar, video
 *  player frame, top app bars). */
fun Modifier.skeuoBrushedMetal(shape: Shape = RoundedCornerShape(0.dp)): Modifier = this
    .clip(shape)
    .background(
        Brush.verticalGradient(
            listOf(
                AeroColors.chromeHighlight,
                AeroColors.chromeMid,
                AeroColors.chromeShadow,
                AeroColors.chromeDeepShadow,
            ),
        ),
    )

/**
 * A glossy, convex "orb" surface — the classic 2008-era glassy button look.
 * Draws a base gradient for the curvature plus a bright highlight ellipse
 * near the top, and lifts the whole thing off the background with a shadow.
 * Pass [pressed] to flatten it (shadow shrinks, gradient inverts slightly)
 * for a real "being pushed in" reaction.
 */
fun Modifier.skeuoGlossyOrb(
    shape: Shape = CircleShape,
    baseColor: Color = AeroColors.chromeMid,
    pressed: Boolean = false,
): Modifier = this
    .shadow(
        elevation = if (pressed) 1.dp else 6.dp,
        shape = shape,
        ambientColor = Color.Black.copy(alpha = 0.5f),
        spotColor = Color.Black.copy(alpha = 0.6f),
    )
    .clip(shape)
    .background(
        Brush.verticalGradient(
            if (pressed) {
                listOf(baseColor.lighten(0.05f), baseColor.darken(0.15f), baseColor.darken(0.3f))
            } else {
                listOf(baseColor.lighten(0.55f), baseColor, baseColor.darken(0.35f))
            },
        ),
    )
    .drawWithContent {
        drawContent()
        if (!pressed) {
            // Gloss highlight: a soft bright ellipse in the upper third, the
            // single detail that reads as "glossy plastic/glass" rather than
            // just "shaded circle".
            drawRoundRectHighlight(this.size.width, this.size.height)
        }
    }

private fun androidx.compose.ui.graphics.drawscope.ContentDrawScope.drawRoundRectHighlight(
    width: Float,
    height: Float,
) {
    val highlightHeight = height * 0.42f
    drawOval(
        brush = Brush.verticalGradient(
            colors = listOf(Color.White.copy(alpha = 0.65f), Color.White.copy(alpha = 0f)),
            startY = height * 0.06f,
            endY = height * 0.06f + highlightHeight,
        ),
        topLeft = Offset(width * 0.14f, height * 0.06f),
        size = androidx.compose.ui.geometry.Size(width * 0.72f, highlightHeight),
    )
}

/**
 * An inset / recessed surface — the opposite of [skeuoGlossyOrb]: shadow
 * reads as coming from *inside* the edges, like a slot a disc sits in, or a
 * sunken screen bezel. Used behind album art on the Now Playing screen and
 * the video-player frame.
 */
fun Modifier.skeuoInsetSurface(shape: Shape = RoundedCornerShape(10.dp)): Modifier = this
    .clip(shape)
    .background(
        Brush.verticalGradient(
            listOf(
                AeroColors.chromeDeepShadow,
                AeroColors.surfaceCharcoalLight,
                AeroColors.chromeMid.copy(alpha = 0.35f),
            ),
        ),
    )
    .drawWithContent {
        drawContent()
        // Dark inner-edge vignette so the surface reads as recessed rather
        // than flat-filled.
        drawRect(
            brush = Brush.verticalGradient(
                colors = listOf(Color.Black.copy(alpha = 0.55f), Color.Transparent),
                endY = size.height * 0.18f,
            ),
        )
        drawRect(
            brush = Brush.verticalGradient(
                colors = listOf(Color.Transparent, Color.Black.copy(alpha = 0.45f)),
                startY = size.height * 0.82f,
            ),
        )
    }

/** Leather header band (Notes) — a warm brown gradient plus a stitched-look
 *  dashed border, evoking a bound notebook cover without tracing any one
 *  app's specific leather-texture photo. */
fun Modifier.skeuoLeatherHeader(): Modifier = this
    .background(
        Brush.verticalGradient(
            listOf(AeroColors.leatherLight, AeroColors.leatherMid, AeroColors.leatherDark),
        ),
    )
    .drawWithContent {
        drawContent()
        // Thin worn highlight along the very top edge.
        drawRect(
            brush = Brush.verticalGradient(
                colors = listOf(Color.White.copy(alpha = 0.18f), Color.Transparent),
                endY = size.height * 0.12f,
            ),
        )
        // Bottom seam shadow where the leather meets the paper.
        drawRect(
            brush = Brush.verticalGradient(
                colors = listOf(Color.Transparent, Color.Black.copy(alpha = 0.35f)),
                startY = size.height * 0.75f,
            ),
        )
    }

/** Legal-pad paper background (Notes body) — cream fill, ruled lines, and a
 *  red margin rule, drawn procedurally rather than a bitmap. */
fun Modifier.skeuoPaper(lineSpacing: Dp = 32.dp): Modifier = this
    .background(AeroColors.paperCream)
    .drawWithContent {
        drawContent()
        val spacingPx = lineSpacing.toPx()
        var y = spacingPx
        while (y < size.height) {
            drawLine(
                color = AeroColors.paperRule,
                start = Offset(0f, y),
                end = Offset(size.width, y),
                strokeWidth = 1.2f,
            )
            y += spacingPx
        }
        drawLine(
            color = AeroColors.paperMargin,
            start = Offset(size.width * 0.11f, 0f),
            end = Offset(size.width * 0.11f, size.height),
            strokeWidth = 1.6f,
        )
    }

/** Frosted/brushed pill used for small floating chrome (toggle buttons, the
 *  Cover Flow view-mode switch). */
fun Modifier.skeuoChromePill(shape: Shape = RoundedCornerShape(50)): Modifier = this
    .shadow(4.dp, shape, ambientColor = Color.Black.copy(alpha = 0.4f), spotColor = Color.Black.copy(alpha = 0.5f))
    .clip(shape)
    .background(
        Brush.verticalGradient(
            listOf(Color.White.copy(alpha = 0.28f), Color.White.copy(alpha = 0.06f)),
        ),
    )

/**
 * Real tactile feedback: scales an element down slightly while pressed, like
 * a physical button being pushed in. A small, generic Compose interaction
 * pattern — written fresh for this project.
 */
@Composable
fun Modifier.pressScale(
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
    pressedScale: Float = 0.94f,
): Modifier {
    val isPressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(
        targetValue = if (isPressed) pressedScale else 1f,
        animationSpec = tween(120),
        label = "pressScale",
    )
    return this.graphicsLayer {
        scaleX = scale
        scaleY = scale
    }
}

private fun Color.lighten(amount: Float): Color = androidx.compose.ui.graphics.lerp(this, Color.White, amount)
private fun Color.darken(amount: Float): Color = androidx.compose.ui.graphics.lerp(this, Color.Black, amount)
