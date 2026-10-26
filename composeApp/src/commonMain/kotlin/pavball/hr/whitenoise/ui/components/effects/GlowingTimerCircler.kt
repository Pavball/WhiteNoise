package pavball.hr.whitenoise.ui.components.effects

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import pavball.hr.whitenoise.ui.theme.DarkNavy
import pavball.hr.whitenoise.ui.theme.LightGray
import pavball.hr.whitenoise.ui.theme.Mint

/**
 * The big glowing circle used by Sleep and Meditations.
 * [glowScale] lets the meditation breathe the glow in and out behind the text.
 */
@Composable
fun GlowingTimerCircle(
    font: FontFamily,
    isActive: Boolean,
    text: String,
    caption: String? = null,
    glowScale: Float = 1f
) {

    val color = if(!isActive) Color.White else Mint
    val textColor = if(isActive) LightGray else DarkNavy // Dark Navy Blue

    Box(contentAlignment = Alignment.Center, modifier = Modifier.size(320.dp)) {
        Box(
            modifier = Modifier
                .size(320.dp)
                .scale(glowScale)
                .background(
                    brush = Brush.radialGradient(
                        // "stops" allow us to control exactly where colors change.
                        // 0.0f is the exact center, 1.0f is the outer edge.
                        colorStops = arrayOf(
                            0.0f to color.copy(alpha = 0.55f), // Very bright center
                            0.4f to color.copy(alpha = 0.55f),  // Still bright 40% out (around text)
                            0.7f to color.copy(alpha = 0.55f),
                            0.8f to color.copy(alpha = 0.55f),
                            0.85f to color.copy(alpha = 0.45f),
                            0.87f to color.copy(alpha = 0.35f),
                            0.92f to color.copy(alpha = 0.25f),   // Starts fading rapidly
                            0.95f to color.copy(alpha = 0.15f),
                            0.97f to color.copy(alpha = 0.10f),
                            1.0f to Color.Transparent                // Completely invisible at edge
                        )
                    ),
                    shape = CircleShape
                )
        )

        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = text,
                fontFamily = font,
                fontSize = 52.sp,
                fontWeight = FontWeight.Light,
                color = textColor
            )
            caption?.let {
                Text(
                    text = it,
                    fontFamily = font,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium,
                    color = textColor.copy(alpha = 0.8f)
                )
            }
        }
    }
}
