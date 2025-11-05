package pavball.hr.whitenoise.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import kotlin.time.Clock
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.Duration.Companion.seconds
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

@OptIn(ExperimentalTime::class)
@Composable
actual fun SleepTimerControl(
    isPlaying: Boolean,
    onTimerFinished: () -> Unit,
    onFadeStart: () -> Unit
) {
    val timerOptions = listOf(5, 10, 15)

    var selectedMinutes by remember { mutableStateOf<Int?>(null) }
    var remainingTime by remember { mutableStateOf(0L) }
    var isRunning by remember { mutableStateOf(false) }
    var fadeOutEnabled by remember { mutableStateOf(true) }
    var fadeStarted by remember { mutableStateOf(false) }

    var endTime by remember { mutableStateOf<Instant?>(null) }

    LaunchedEffect(isPlaying, selectedMinutes) {
        if (selectedMinutes != null) {
            if (isPlaying && !isRunning && remainingTime > 0) {
                // Resume timer from remaining time
                endTime = Clock.System.now().plus(remainingTime.milliseconds)
                isRunning = true
            } else if (isPlaying && !isRunning && remainingTime == 0L) {
                // Fresh start
                val minutes = selectedMinutes!!
                remainingTime = (minutes * 60 * 1000).toLong()
                endTime = Clock.System.now().plus(remainingTime.milliseconds)
                fadeStarted = false
                isRunning = true
            } else if (!isPlaying && isRunning) {
                // Pause timer — don’t reset, just stop counting
                isRunning = false
                endTime = null
            }

            while (isRunning && isPlaying && remainingTime > 0) {
                delay(1.seconds)
                val now = kotlin.time.Clock.System.now()
                val remaining = endTime?.let { it - now } ?: continue
                remainingTime = remaining.inWholeMilliseconds.coerceAtLeast(0)

                if (fadeOutEnabled && !fadeStarted && remainingTime <= 30_000L) {
                    fadeStarted = true
                    onFadeStart()
                }
            }

            if (remainingTime == 0L && isRunning) {
                isRunning = false
                onTimerFinished()
            }
        }
    }

    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text("Sleep Timer")

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            timerOptions.forEach { minutes ->
                Button(
                    onClick = {
                        selectedMinutes = minutes
                        remainingTime = 0L // reset remaining if new timer chosen
                        isRunning = false
                        fadeStarted = false
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (selectedMinutes == minutes)
                            MaterialTheme.colorScheme.primary
                        else MaterialTheme.colorScheme.secondaryContainer
                    )
                ) {
                    Text("$minutes min")
                }
            }

            if (isRunning || selectedMinutes != null) {
                Button(onClick = {
                    selectedMinutes = null
                    isRunning = false
                    remainingTime = 0L
                }) {
                    Text("Cancel")
                }
            }
        }

        Row(verticalAlignment = Alignment.CenterVertically) {
            Checkbox(
                checked = fadeOutEnabled,
                onCheckedChange = { fadeOutEnabled = it }
            )
            Text("Fade out last 30 seconds")
        }

        if (selectedMinutes != null && remainingTime > 0) {
            val minutes = (remainingTime / 60000)
            val seconds = (remainingTime / 1000) % 60
            Text("Stopping in %02d:%02d".format(minutes, seconds))
        } else if (selectedMinutes != null && !isRunning) {
            Text("Timer set: $selectedMinutes min (waiting for playback)")
        }
    }
}