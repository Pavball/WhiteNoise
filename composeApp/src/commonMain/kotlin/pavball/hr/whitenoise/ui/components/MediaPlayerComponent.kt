package pavball.hr.whitenoise.ui.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
expect fun MediaPlayerComponent(
    modifier: Modifier = Modifier,
    onFadeStart: () -> Unit = {},
    onTimerFinished: () -> Unit = {}
)