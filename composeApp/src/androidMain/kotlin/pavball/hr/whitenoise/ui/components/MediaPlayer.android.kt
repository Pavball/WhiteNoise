package pavball.hr.whitenoise.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.koin.androidx.compose.koinViewModel
import pavball.hr.whitenoise.ui.viewmodels.MainScreenViewModel
import pavball.hr.whitenoise.ui.viewmodels.MainScreenViewState
import pavball.hr.whitenoise.viewmodels.MainScreenViewModelImpl

@Composable
actual fun MediaPlayerComponent(
    modifier: Modifier,
    onFadeStart: () -> Unit,
    onTimerFinished: () -> Unit
) {
    val viewModel = koinViewModel<MainScreenViewModelImpl>()
    val state by viewModel.viewState<MainScreenViewState>()
        .collectAsState(initial = MainScreenViewState.Initial)

// derive playback state
    val isPlaying = (state as? MainScreenViewState.PlayerState)?.isPlaying ?: false

    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("White Noise Player", style = MaterialTheme.typography.titleLarge)
        Spacer(Modifier.height(16.dp))

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Button(onClick = { viewModel.playSound("rain") }) { Text("Rain") }
            Button(onClick = { viewModel.playSound("forest") }) { Text("Forest") }
            Button(onClick = { viewModel.playSound("ocean") }) { Text("Ocean") }
        }

        Spacer(Modifier.height(16.dp))

        Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            IconButton(onClick = {
                if (isPlaying) viewModel.pauseSound()
                else viewModel.playSound((state as? MainScreenViewState.PlayerState)?.currentSound ?: "rain")
            }) {
                Icon(
                    imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                    contentDescription = if (isPlaying) "Pause" else "Play"
                )
            }
            Button(onClick = { viewModel.stopSound() }) {
                Text("Stop")
            }
        }

        Spacer(Modifier.height(24.dp))

        SleepTimerControl(
            isPlaying = isPlaying,
            onFadeStart = onFadeStart,
            onTimerFinished = {
                onTimerFinished()
                viewModel.stopSound()
            }
        )
    }
}


