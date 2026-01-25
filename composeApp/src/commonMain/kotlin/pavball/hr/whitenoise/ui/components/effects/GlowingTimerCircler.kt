package pavball.hr.whitenoise.ui.components.effects

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import pavball.hr.whitenoise.ui.theme.DarkNavy
import pavball.hr.whitenoise.ui.theme.LightGray
import pavball.hr.whitenoise.ui.theme.Mint

@Composable
fun GlowingTimerCircle(font: FontFamily, isActive: Boolean) {

    val color = if(!isActive) Color.White else Mint

    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .size(320.dp)
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
    ) {
        Text(
            text = "00:00:00",
            fontFamily = font,
            fontSize = 52.sp,
            fontWeight = FontWeight.Light,
            color = if(isActive) LightGray else DarkNavy // Dark Navy Blue
        )
    }
}