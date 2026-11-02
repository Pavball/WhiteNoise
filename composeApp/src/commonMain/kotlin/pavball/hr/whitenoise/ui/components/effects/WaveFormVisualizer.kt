package pavball.hr.whitenoise.ui.components.effects

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import pavball.hr.whitenoise.ui.components.utils.lighten
import pavball.hr.whitenoise.ui.components.utils.resolveGradientColors
import kotlin.math.PI
import kotlin.math.sin

private val NO_LEVELS = FloatArray(0)

/**
 * Bars that follow the sound that is playing: [levels] gives the loudness of
 * each frequency band (low to high, 0..1), drawn mirrored so the bass sits in
 * the middle. Paused, or without live levels, the bars move in a calm wave.
 *
 * [levels] is read while drawing, so new levels only redraw the canvas.
 */
@Composable
fun WaveformVisualizer(
    isPlaying: Boolean,
    soundType: String?, // e.g. "ocean", "forest", "rain"
    modifier: Modifier = Modifier,
    customColor: Color? = null, // a custom sound's own color, overrides soundType
    levels: () -> FloatArray = { NO_LEVELS }
) {
    val infiniteTransition = rememberInfiniteTransition(label = "wave")

    // Amplitude of the synthetic wave; also how strongly the bars glow
    val breath by infiniteTransition.animateFloat(
        initialValue = 0.3f,
        targetValue = if (isPlaying) 1f else 0.55f,
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = if (isPlaying) 800 else 2400,
                easing = FastOutSlowInEasing
            ),
            repeatMode = RepeatMode.Reverse
        ),
        label = "breath"
    )

    // Horizontal motion of the synthetic wave
    val phaseShift by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = (2f * PI).toFloat(),
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = if (isPlaying) 1500 else 5000,
                easing = LinearEasing
            ),
            repeatMode = RepeatMode.Restart
        ),
        label = "phaseShift"
    )

    val (baseColor, lightColor) = if (customColor != null) {
        customColor to customColor.lighten(0.35f)
    } else {
        resolveGradientColors(soundType, null).let { it[0] to it[1] }
    }

    Canvas(modifier = modifier.fillMaxWidth().height(80.dp)) {
        val data = levels()
        val live = isPlaying && data.isNotEmpty()
        val barCount = if (live) data.size * 2 else SYNTHETIC_BARS
        val half = barCount / 2
        val slot = size.width / barCount
        val barWidth = slot * 0.55f
        val corner = CornerRadius(barWidth / 2f)
        val glowPad = 3.dp.toPx()
        val glowAlpha = if (isPlaying) 0.22f else 0.12f

        // Short bars stay in the base color, tall ones reach the light tips.
        val barBrush = Brush.verticalGradient(
            listOf(lightColor, baseColor, lightColor),
            startY = 0f,
            endY = size.height
        )

        for (i in 0 until barCount) {
            val level = if (live) {
                val band = if (i >= half) i - half else half - 1 - i
                MIN_LEVEL + (1f - MIN_LEVEL) * data[band]
            } else {
                val wave = (sin(phaseShift + i * 0.35f) + 1f) / 2f
                breath * (0.35f + 0.65f * wave)
            }
            val barHeight = (size.height * level * 0.9f).coerceAtLeast(barWidth)
            val x = i * slot + (slot - barWidth) / 2f
            val y = (size.height - barHeight) / 2f

            drawRoundRect(
                color = lightColor.copy(alpha = glowAlpha),
                topLeft = Offset(x - glowPad, y - glowPad),
                size = Size(barWidth + glowPad * 2f, barHeight + glowPad * 2f),
                cornerRadius = CornerRadius(corner.x + glowPad)
            )
            drawRoundRect(
                brush = barBrush,
                topLeft = Offset(x, y),
                size = Size(barWidth, barHeight),
                cornerRadius = corner
            )
        }
    }
}

private const val SYNTHETIC_BARS = 32
private const val MIN_LEVEL = 0.06f
