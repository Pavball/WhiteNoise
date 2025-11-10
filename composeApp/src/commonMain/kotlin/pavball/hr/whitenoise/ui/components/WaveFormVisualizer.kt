package pavball.hr.whitenoise.ui.components

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

@Composable
fun WaveformVisualizer(
    isPlaying: Boolean,
    soundType: String?, // e.g. "ocean", "forest", "rain"
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "wave")

    // 🎵 Animate amplitude differently based on play state
    val amplitude by infiniteTransition.animateFloat(
        initialValue = if (isPlaying) 0.3f else 0.5f,
        targetValue = if (isPlaying) 1f else 0.7f,
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = if (isPlaying) 500 else 2500, // faster beat vs gentle pulse
                easing = LinearEasing
            ),
            repeatMode = RepeatMode.Reverse
        ),
        label = "amplitude"
    )

    // 🌈 Dynamic gradient based on sound type
    val gradient = when (soundType) {
        "ocean" -> Brush.verticalGradient(
            listOf(Color(0xFF42A5F5), Color(0xFF1976D2))
        )
        "forest" -> Brush.verticalGradient(
            listOf(Color(0xFF66BB6A), Color(0xFF2E7D32))
        )
        "rain" -> Brush.verticalGradient(
            listOf(Color(0xFF90A4AE), Color(0xFF546E7A))
        )
        else -> Brush.verticalGradient(
            listOf(Color(0xFF7986CB), Color(0xFF3F51B5))
        )
    }

    Canvas(modifier = modifier.fillMaxWidth().height(100.dp)) {
        val barCount = 20
        val barWidth = size.width / barCount

        for (i in 0 until barCount) {
            // Slight staggered variation between bars for a more natural look
            val variation = (i % 5) * 0.1f
            val heightFactor = amplitude * (0.5f + variation)
            val barHeight = size.height * heightFactor

            drawRect(
                brush = gradient,
                topLeft = Offset(i * barWidth, size.height - barHeight),
                size = Size(barWidth * 0.6f, barHeight)
            )
        }
    }
}
