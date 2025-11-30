package pavball.hr.whitenoise.ui.components.sections.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import pavball.hr.whitenoise.ui.components.WaveformVisualizer

@Composable
fun NowPlayingCardSection(
    soundLabel: String,
    soundKey: String?,
    isPlaying: Boolean,
    subLabel: String? = null,
    listOfColors: List<Color> = listOf(Color(0xFF4FC3F7)),
    modifier: Modifier = Modifier
) {
    val gradient = Brush.verticalGradient(
        colors = listOfColors
    )

    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier
                .background(gradient)
                .height(120.dp)
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.Start,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(
                    text = soundLabel,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                subLabel?.let {
                    Spacer(Modifier.height(4.dp))
                    Text(
                        text = it,
                        color = Color.White.copy(alpha = 0.9f)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Add animated waveform
            WaveformVisualizer(
                isPlaying = isPlaying,
                soundType = soundKey,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(40.dp)
            )
        }
    }
}
