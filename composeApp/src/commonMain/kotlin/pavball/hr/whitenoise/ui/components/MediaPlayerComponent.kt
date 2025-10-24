package pavball.hr.whitenoise.ui.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
expect fun MediaPlayerComponent(
    modifier: Modifier = Modifier,
    url: String,
    start: Boolean,
    pause: Boolean,
    stop: Boolean,
    onLoadingChanged: (Boolean) -> Unit = {}
)