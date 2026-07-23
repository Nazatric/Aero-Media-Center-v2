package com.aeromedia.app.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.produceState
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import java.util.Calendar
import kotlin.math.cos
import kotlin.math.sin

/**
 * A real, live-updating analog clock face: amber/orange metal bezel, white
 * dial, black tapered hour/minute hands, thin red second hand — matching the
 * reference screenshot's clock widget — drawn on Canvas, with an optional
 * faded mirror reflection beneath it. Ticks once a second.
 */
@Composable
fun AnalogClockFace(modifier: Modifier = Modifier, sizeDp: Int = 64, showReflection: Boolean = false) {
    if (!showReflection) {
        ClockFaceCanvas(modifier = modifier.size(sizeDp.dp))
        return
    }
    Column(modifier = modifier) {
        ClockFaceCanvas(modifier = Modifier.size(sizeDp.dp))
        // Reflection: a full copy of the clock, flipped vertically around its
        // own center, viewed through a half-height clipped window aligned to
        // its top — which shows exactly the (now-mirrored) bottom half of the
        // original, per the standard "reflection" trick.
        Box(
            modifier = Modifier
                .size(width = sizeDp.dp, height = (sizeDp / 2).dp)
                .clipToBounds()
                .alpha(0.3f),
        ) {
            ClockFaceCanvas(
                modifier = Modifier
                    .size(sizeDp.dp)
                    .graphicsLayer { scaleY = -1f }
                    .drawWithContent {
                        drawContent()
                        drawRect(brush = Brush.verticalGradient(listOf(Color.Transparent, Color.Black)))
                    },
            )
        }
    }
}

@Composable
private fun ClockFaceCanvas(modifier: Modifier = Modifier) {
    val now by produceState(initialValue = Calendar.getInstance()) {
        while (true) {
            value = Calendar.getInstance()
            delay(1000)
        }
    }

    Canvas(modifier = modifier) {
        val radius = size.minDimension / 2f
        val center = Offset(size.width / 2f, size.height / 2f)

        // Bezel: amber/orange metal ring.
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(Color(0xFFFFE0A3), Color(0xFFF3A83C), Color(0xFFB5691A)),
                center = Offset(center.x - radius * 0.2f, center.y - radius * 0.2f),
                radius = radius * 1.4f,
            ),
            radius = radius,
            center = center,
        )
        // White dial face.
        drawCircle(color = Color(0xFFF7F7F5), radius = radius * 0.83f, center = center)
        // Glass highlight, upper-left.
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(Color.White.copy(alpha = 0.7f), Color.White.copy(alpha = 0f)),
                center = Offset(center.x - radius * 0.32f, center.y - radius * 0.38f),
                radius = radius * 0.6f,
            ),
            radius = radius * 0.83f,
            center = center,
        )
        // Tick marks.
        for (i in 0 until 12) {
            val angle = Math.toRadians((i * 30 - 90).toDouble())
            val outer = Offset(center.x + (radius * 0.78f * cos(angle)).toFloat(), center.y + (radius * 0.78f * sin(angle)).toFloat())
            val inner = Offset(center.x + (radius * 0.68f * cos(angle)).toFloat(), center.y + (radius * 0.68f * sin(angle)).toFloat())
            drawLine(color = Color(0xFF2B2B2B), start = inner, end = outer, strokeWidth = if (i % 3 == 0) 2.4f else 1.3f, cap = StrokeCap.Round)
        }

        val calendar = now
        val hour = calendar.get(Calendar.HOUR)
        val minute = calendar.get(Calendar.MINUTE)
        val second = calendar.get(Calendar.SECOND)

        drawHand(center, radius * 0.42f, ((hour % 12) + minute / 60f) / 12f * 360f, 3.6f, Color(0xFF232323))
        drawHand(center, radius * 0.64f, (minute + second / 60f) / 60f * 360f, 2.6f, Color(0xFF232323))
        drawHand(center, radius * 0.70f, second / 60f * 360f, 1.2f, Color(0xFFE0521C))

        drawCircle(color = Color(0xFFE0521C), radius = radius * 0.055f, center = center)
    }
}

private fun androidx.compose.ui.graphics.drawscope.DrawScope.drawHand(
    center: Offset,
    length: Float,
    angleDegrees: Float,
    strokeWidth: Float,
    color: Color,
) {
    val angleRad = Math.toRadians((angleDegrees - 90).toDouble())
    val end = Offset(
        x = center.x + (length * cos(angleRad)).toFloat(),
        y = center.y + (length * sin(angleRad)).toFloat(),
    )
    drawLine(
        color = color,
        start = center,
        end = end,
        strokeWidth = strokeWidth,
        cap = StrokeCap.Round,
    )
}
