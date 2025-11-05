package pavball.hr.whitenoise.ui.components

import androidx.compose.runtime.Composable
import kotlin.time.ExperimentalTime

@OptIn(markerClass = [ExperimentalTime::class])
@Composable
actual fun SleepTimerControl(isPlaying: Boolean, onTimerFinished: () -> Unit, onFadeStart: () -> Unit) {
}