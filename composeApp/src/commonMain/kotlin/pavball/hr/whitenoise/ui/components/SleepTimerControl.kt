package pavball.hr.whitenoise.ui.components

import androidx.compose.runtime.Composable
import kotlin.time.ExperimentalTime

@OptIn(ExperimentalTime::class)
@Composable
expect fun SleepTimerControl(
    isPlaying: Boolean,
    onTimerFinished: () -> Unit,
    onFadeStart: () -> Unit
)


