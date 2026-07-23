package com.aeromedia.app.ui.components

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.aeromedia.app.ui.theme.AeroColors
import com.aeromedia.app.ui.theme.skeuoGlossyOrb
import com.aeromedia.app.ui.theme.skeuoInsetSurface
import com.aeromedia.app.util.LocalSoundEffects

/** A glossy, physically-sliding toggle switch — an inset track (so it reads
 *  as a slot the knob sits in) with a raised glossy knob that slides and
 *  changes color with state, rather than a flat Material Switch. */
@Composable
fun SkeuoToggleSwitch(
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
) {
    val soundEffects = LocalSoundEffects.current
    val knobOffset by animateDpAsState(targetValue = if (checked) 24.dp else 2.dp, animationSpec = tween(150), label = "knobOffset")

    Box(
        modifier = modifier
            .size(width = 50.dp, height = 28.dp)
            .clickable {
                soundEffects.toggle()
                onCheckedChange(!checked)
            }
            .skeuoInsetSurface(RoundedCornerShape(50)),
    ) {
        Box(
            modifier = Modifier
                .offset(x = knobOffset, y = 2.dp)
                .size(24.dp)
                .skeuoGlossyOrb(
                    shape = CircleShape,
                    baseColor = if (checked) AeroColors.accentGreen else AeroColors.chromeMid,
                ),
        )
    }
}
