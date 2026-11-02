package pavball.hr.whitenoise.ui.components.input

import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import pavball.hr.whitenoise.ui.theme.Mint

/**
 * A 0..1 volume slider. While dragging it shows the finger's value, so the
 * stored volume catching up a moment later can't make the thumb jump back.
 */
@Composable
fun DozzzeVolumeSlider(
    value: Float,
    onValueChange: (Float) -> Unit,
    modifier: Modifier = Modifier,
    color: Color = Mint,
    enabled: Boolean = true
) {
    var dragValue by remember { mutableStateOf<Float?>(null) }
    Slider(
        value = dragValue ?: value,
        onValueChange = { raw ->
            val snapped = if (raw < SNAP_TO_ZERO) 0f else raw
            dragValue = snapped
            onValueChange(snapped)
        },
        onValueChangeFinished = { dragValue = null },
        enabled = enabled,
        colors = SliderDefaults.colors(
            thumbColor = color,
            activeTrackColor = color,
            inactiveTrackColor = Color.White.copy(alpha = 0.15f),
            disabledThumbColor = Color.White.copy(alpha = 0.3f),
            disabledActiveTrackColor = Color.White.copy(alpha = 0.3f),
            disabledInactiveTrackColor = Color.White.copy(alpha = 0.1f)
        ),
        modifier = modifier
    )
}

private const val SNAP_TO_ZERO = 0.03f
