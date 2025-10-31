package pavball.hr.whitenoise.ui.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
expect fun MediaPlayerComponent(
    modifier: Modifier = Modifier,
    resId: String,
    start: Boolean,
    pause: Boolean,
    stop: Boolean
)