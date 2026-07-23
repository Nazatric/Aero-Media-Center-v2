package com.aeromedia.app.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.SkipNext
import androidx.compose.material.icons.filled.SkipPrevious
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.unit.dp
import com.aeromedia.app.ui.theme.AeroColors
import com.aeromedia.app.ui.theme.pressScale
import com.aeromedia.app.ui.theme.skeuoGlossyOrb
import com.aeromedia.app.util.LocalSoundEffects

/** Glossy play/pause + skip transport row, shared by the Now Playing and
 *  Video Player screens. Play/pause is drawn as vector shapes rather than a
 *  Material icon so the glyph itself reads as part of the glossy orb. */
@Composable
fun TransportBar(
    isPlaying: Boolean,
    onPlayPause: () -> Unit,
    onSkipNext: () -> Unit,
    onSkipPrevious: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        TransportGlossyButton(sizeDp = 40, onClick = onSkipPrevious) {
            Icon(Icons.Filled.SkipPrevious, contentDescription = "Previous", tint = AeroColors.surfaceCharcoal)
        }
        Box(modifier = Modifier.padding(horizontal = 14.dp)) {
            TransportGlossyButton(sizeDp = 58, onClick = onPlayPause) {
                PlayPauseGlyph(isPlaying = isPlaying)
            }
        }
        TransportGlossyButton(sizeDp = 40, onClick = onSkipNext) {
            Icon(Icons.Filled.SkipNext, contentDescription = "Next", tint = AeroColors.surfaceCharcoal)
        }
    }
}

@Composable
private fun TransportGlossyButton(
    sizeDp: Int,
    onClick: () -> Unit,
    content: @Composable () -> Unit,
) {
    val interactionSource = remember { MutableInteractionSource() }
    val soundEffects = LocalSoundEffects.current
    Box(
        modifier = Modifier
            .size(sizeDp.dp)
            .pressScale(interactionSource)
            .clickable(interactionSource = interactionSource, indication = null, onClick = {
                soundEffects.toggle()
                onClick()
            })
            .skeuoGlossyOrb(shape = CircleShape, baseColor = AeroColors.chromeMid),
        contentAlignment = Alignment.Center,
    ) {
        content()
    }
}

@Composable
private fun PlayPauseGlyph(isPlaying: Boolean) {
    Canvas(modifier = Modifier.size(22.dp)) {
        val color = AeroColors.surfaceCharcoal
        if (isPlaying) {
            val barWidth = size.width * 0.26f
            drawRoundRect(color = color, topLeft = Offset(size.width * 0.16f, 0f), size = androidx.compose.ui.geometry.Size(barWidth, size.height), cornerRadius = androidx.compose.ui.geometry.CornerRadius(2f, 2f))
            drawRoundRect(color = color, topLeft = Offset(size.width * 0.58f, 0f), size = androidx.compose.ui.geometry.Size(barWidth, size.height), cornerRadius = androidx.compose.ui.geometry.CornerRadius(2f, 2f))
        } else {
            val path = Path().apply {
                moveTo(size.width * 0.12f, 0f)
                lineTo(size.width * 0.12f, size.height)
                lineTo(size.width * 0.92f, size.height * 0.5f)
                close()
            }
            drawPath(path, color = color)
        }
    }
}
