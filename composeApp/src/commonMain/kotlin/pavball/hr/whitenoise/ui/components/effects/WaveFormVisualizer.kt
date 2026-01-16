package pavball.hr.whitenoise.ui.components.effects

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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import kotlin.math.PI
import kotlin.math.sin

@Composable
fun WaveformVisualizer(
    isPlaying: Boolean,
    soundType: String?, // e.g. "ocean", "forest", "rain"
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "wave")

    // Base amplitude that oscillates (drives both bar height and glow intensity)
    val baseAmplitude by infiniteTransition.animateFloat(
        initialValue = 0.3f,
        targetValue = if (isPlaying) 1f else 0.6f,
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = if (isPlaying) 800 else 2000,
                easing = LinearEasing
            ),
            repeatMode = RepeatMode.Reverse
        ),
        label = "baseAmplitude"
    )

    // Horizontal phase motion
    val phaseShift by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = (2f * PI).toFloat(),
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = if (isPlaying) 1500 else 4000,
                easing = LinearEasing
            ),
            repeatMode = RepeatMode.Restart
        ),
        label = "phaseShift"
    )

    // Gradients per sound type
    val (mainGradient, glowColor) = when (soundType) {
        "ocean" -> Brush.verticalGradient(listOf(Color(0xFF42A5F5), Color(0xFF1976D2))) to Color(0xFF42A5F5)
        "forest" -> Brush.verticalGradient(listOf(Color(0xFF66BB6A), Color(0xFF2E7D32))) to Color(0xFF66BB6A)
        "rain" -> Brush.verticalGradient(listOf(Color(0xFF90A4AE), Color(0xFF546E7A))) to Color(0xFF90A4AE)
        else -> Brush.verticalGradient(listOf(Color(0xFF7986CB), Color(0xFF3F51B5))) to Color(0xFF7986CB)
    }

    Canvas(modifier = modifier.fillMaxWidth().height(80.dp)) {
        val barCount = 32
        val barWidth = size.width / barCount
        val centerY = size.height / 2f

        // Glow layers: draw a few translucent rects behind each bar with increasing size and decreasing alpha
        for (i in 0 until barCount) {
            val localPhase = phaseShift + (i * 0.35f)
            val waveOffset = sin(localPhase)
            val heightFactor = baseAmplitude * (0.5f + (waveOffset + 1f) / 2f)
            val barHeight = size.height * heightFactor * 0.9f

            val x = i * barWidth
            val y = centerY - barHeight / 2
            val w = barWidth * 0.6f
            val h = barHeight

            // glow intensity scales with amplitude (so playing = stronger glow)
            val glowIntensity = (0.08f + 0.4f * baseAmplitude).coerceIn(0.02f, 0.6f)

            // Draw 3 glow layers (furthest = largest & faintest)
            val glowLayers = listOf(
                Pair(10f, glowIntensity * 0.18f), // outer
                Pair(6f, glowIntensity * 0.32f),  // mid
                Pair(2f, glowIntensity * 0.6f)    // inner
            )

            for ((offsetPadding, alpha) in glowLayers) {
                drawRect(
                    color = glowColor.copy(alpha = alpha),
                    topLeft = Offset(x - offsetPadding, y - offsetPadding),
                    size = Size(w + offsetPadding * 2f, h + offsetPadding * 2f)
                )
            }
        }

        // Main bars over the glow
        for (i in 0 until barCount) {
            val localPhase = phaseShift + (i * 0.35f)
            val waveOffset = sin(localPhase)
            val heightFactor = baseAmplitude * (0.5f + (waveOffset + 1f) / 2f)
            val barHeight = size.height * heightFactor * 0.9f

            drawRect(
                brush = mainGradient,
                topLeft = Offset(x = i * barWidth, y = centerY - barHeight / 2),
                size = Size(barWidth * 0.6f, barHeight)
            )
        }
    }
}
