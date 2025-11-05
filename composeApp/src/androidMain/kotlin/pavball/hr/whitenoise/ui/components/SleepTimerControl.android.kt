package pavball.hr.whitenoise.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.flow.collectLatest
import org.koin.androidx.compose.koinViewModel
import pavball.hr.whitenoise.ui.viewmodels.MainScreenViewModel
import pavball.hr.whitenoise.ui.viewmodels.MainScreenViewState
import pavball.hr.whitenoise.viewmodels.MainScreenViewModelImpl
import kotlin.time.ExperimentalTime

@OptIn(ExperimentalTime::class)
@Composable
actual fun SleepTimerControl(
    isPlaying: Boolean,
    onFadeStart: () -> Unit,
    onTimerFinished: () -> Unit
) {


    val sleepTimerViewModel = koinViewModel<MainScreenViewModel>()
    var state by sleepTimerViewModel.viewState<MainScreenViewState>()
        .collectAsState(initial = MainScreenViewState.Initial)

    var fadeEnabled by remember { mutableStateOf(true) }

    LaunchedEffect(Unit) {
        sleepTimerViewModel.viewState<MainScreenViewState>().collectLatest { state = it }
    }

    LaunchedEffect(isPlaying) {
        if (!isPlaying && state is MainScreenViewState.TimerRunning) {
            sleepTimerViewModel.pauseTimer()
        } else if (isPlaying && state is MainScreenViewState.TimerPaused) {
            sleepTimerViewModel.resumeTimer(onFadeStart, onTimerFinished)
        }
    }

    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text("Sleep Timer", style = MaterialTheme.typography.titleMedium)

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            listOf(5, 10, 15).forEach { minutes ->
                Button(onClick = {
                    sleepTimerViewModel.startTimer(minutes, fadeEnabled, onFadeStart, onTimerFinished)
                }) {
                    Text("$minutes min")
                }
            }
            Button(onClick = { sleepTimerViewModel.cancelTimer() }) { Text("Cancel") }
        }

        Row(verticalAlignment = Alignment.CenterVertically) {
            Checkbox(checked = fadeEnabled, onCheckedChange = { fadeEnabled = it })
            Text("Fade out last 30 seconds")
        }

        when (val s = state) {
            is MainScreenViewState.TimerRunning -> {
                val m = s.remainingTime / 60000
                val sec = (s.remainingTime / 1000) % 60
                Text("Stopping in %02d:%02d".format(m, sec))
            }
            is MainScreenViewState.TimerPaused -> {
                val m = s.remainingTime / 60000
                val sec = (s.remainingTime / 1000) % 60
                Text("Paused at %02d:%02d".format(m, sec))
            }
            MainScreenViewState.TimerFinished -> Text("Timer finished")
            MainScreenViewState.Initial -> {}
            is MainScreenViewState.PlayerState -> {}
        }
    }
}