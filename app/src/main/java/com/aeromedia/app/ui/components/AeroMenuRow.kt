package com.aeromedia.app.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.aeromedia.app.ui.theme.AeroColors
import com.aeromedia.app.ui.theme.pressScale
import com.aeromedia.app.util.LocalSoundEffects

/**
 * One row of the Home screen's vertical menu: a flat white icon glyph with a
 * soft drop shadow directly on the background, an all-caps label to its
 * right — no badge/container behind the icon, matching the reference
 * screenshot exactly rather than the earlier glossy-badge version.
 *
 * [icon] is a content slot rather than a fixed `ImageVector` type so a row
 * can use either a Material Symbol (via the [AeroMenuRow] overload below) or
 * one of the bundled Crystal Project bitmaps (via `Image(painterResource(...))`).
 */
@Composable
fun AeroMenuRow(
    label: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    icon: @Composable () -> Unit,
) {
    val interactionSource = remember { MutableInteractionSource() }
    val soundEffects = LocalSoundEffects.current
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clickable(interactionSource = interactionSource, indication = null, onClick = {
                soundEffects.tap()
                onClick()
            })
            .padding(horizontal = 24.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            modifier = Modifier
                .size(48.dp)
                .pressScale(interactionSource),
            contentAlignment = Alignment.Center,
        ) {
            icon()
        }
        Text(
            text = label.uppercase(),
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.SemiBold,
            fontSize = 26.sp,
            color = AeroColors.textPrimary,
            textAlign = TextAlign.Start,
            modifier = Modifier
                .padding(start = 22.dp)
                .shadow(elevation = 6.dp, shape = RectangleShape, ambientColor = Color.Black, spotColor = Color.Black),
        )
    }
}

/** Convenience overload for a Material Symbol glyph, drawn flat white with a
 *  soft drop shadow (drawn twice — a dark offset copy behind a white one —
 *  since Modifier.shadow follows an icon's bounding box, not its silhouette). */
@Composable
fun AeroMenuRow(
    icon: ImageVector,
    label: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    AeroMenuRow(label = label, onClick = onClick, modifier = modifier) {
        ShadowedGlyph { tint, mod -> Icon(imageVector = icon, contentDescription = null, tint = tint, modifier = mod) }
    }
}

/** Same drop-shadow trick as [ShadowedGlyph], for the bundled Crystal
 *  Project bitmaps: a darkened offset copy (via a tint color filter) behind
 *  the real full-color icon. */
@Composable
fun AeroMenuBitmapIcon(painter: androidx.compose.ui.graphics.painter.Painter) {
    Box(modifier = Modifier.size(34.dp), contentAlignment = Alignment.Center) {
        Image(
            painter = painter,
            contentDescription = null,
            colorFilter = androidx.compose.ui.graphics.ColorFilter.tint(Color.Black.copy(alpha = 0.45f)),
            modifier = Modifier.offset(1.5.dp, 2.dp).size(34.dp),
        )
        Image(painter = painter, contentDescription = null, modifier = Modifier.size(34.dp))
    }
}

/** Draws [content] twice at the given size — a soft dark offset copy first,
 *  a bright copy on top — producing a drop shadow that follows the actual
 *  glyph shape rather than a rectangular bounding box. */
@Composable
private fun ShadowedGlyph(content: @Composable (tint: Color, modifier: Modifier) -> Unit) {
    Box(contentAlignment = Alignment.Center) {
        content(Color.Black.copy(alpha = 0.45f), Modifier.offset(1.5.dp, 2.dp).size(30.dp))
        content(Color(0xFFF3F6FA), Modifier.size(30.dp))
    }
}
