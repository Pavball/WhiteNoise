package pavball.hr.whitenoise.ui.screens.home

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import org.jetbrains.compose.resources.painterResource
import org.koin.compose.viewmodel.koinViewModel
import pavball.hr.whitenoise.ui.components.formatTime
import pavball.hr.whitenoise.ui.viewmodels.MainScreenViewModel
import pavball.hr.whitenoise.ui.viewmodels.MainScreenViewState
import whitenoise.composeapp.generated.resources.Res
import whitenoise.composeapp.generated.resources.ic_pause
import whitenoise.composeapp.generated.resources.ic_play
import whitenoise.composeapp.generated.resources.ic_stop

@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    navController: NavController
) {
    val viewModel = koinViewModel<MainScreenViewModel>()
    val state by viewModel.viewState<MainScreenViewState>()
        .collectAsState(initial = MainScreenViewState.Initial)

    var expanded by remember { mutableStateOf(false) }
    var fadeEnabled by remember { mutableStateOf(true) }

    val sounds = listOf(
        "Rain" to "rain",
        "Ocean Waves" to "ocean",
        "Forest Ambience" to "forest"
    )

    val selectedSoundKey = (state as? MainScreenViewState.PlayerState)?.currentSound ?: "rain"
    val selectedSoundLabel = sounds.firstOrNull { it.second == selectedSoundKey }?.first ?: "Rain"

    val isPlaying = (state as? MainScreenViewState.PlayerState)?.isPlaying ?: false
    val isLoading = (state as? MainScreenViewState.PlayerState)?.isLoading ?: false

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // 🎵 SOUND SELECTOR
        Button(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp),
            onClick = { expanded = !expanded },
            colors = ButtonDefaults.buttonColors(containerColor = Color.Blue.copy(alpha = 0.5f)),
            shape = RoundedCornerShape(12.dp),
            border = BorderStroke(1.dp, Color.Black.copy(alpha = 0.75f))
        ) {
            Text(selectedSoundLabel)
            DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                sounds.forEach { (label, soundId) ->
                    DropdownMenuItem(
                        modifier = Modifier
                            .height(32.dp)
                            .width(512.dp)
                            .padding(horizontal = 6.dp)
                            .clip(RoundedCornerShape(8.dp)),
                        text = { Text(label, fontWeight = FontWeight.Bold) },
                        onClick = {
                            expanded = false
                            viewModel.playSound(soundId)
                        }
                    )
                }
            }
        }

        Spacer(Modifier.height(24.dp))

        // 🎧 CONTROLS
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            IconButton(
                onClick = {
                    if (isPlaying) viewModel.pauseSound()
                    else viewModel.playSound(selectedSoundKey)
                },
            ) {
                Icon(
                    painter = painterResource(
                        if (isPlaying) Res.drawable.ic_pause else Res.drawable.ic_play
                    ),
                    contentDescription = if (isPlaying) "Pause" else "Play",
                    tint = if (isLoading) Color.Gray else Color.Blue
                )
            }

            IconButton(
                onClick = { viewModel.stopSound() },
            ) {
                Icon(
                    painter = painterResource(Res.drawable.ic_stop),
                    contentDescription = "Stop",
                    tint = if (isLoading) Color.Gray else Color.Blue
                )
            }
        }

        Spacer(Modifier.height(24.dp))

        // ⏰ TIMER
        Text("Sleep Timer", style = MaterialTheme.typography.titleMedium)

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            listOf(5, 10, 15).forEach { minutes ->
                Button(onClick = { viewModel.startTimer(minutes, fadeEnabled) }) {
                    Text("$minutes min")
                }
            }

            Button(onClick = { viewModel.cancelTimer() }) {
                Text("Cancel")
            }
        }


        Row(verticalAlignment = Alignment.CenterVertically) {
            Checkbox(
                checked = fadeEnabled,
                onCheckedChange = { fadeEnabled = it }
            )
            Text("Fade out last 30s")
        }

        when (val s = state) {
            is MainScreenViewState.TimerRunning -> {
                Text("Stopping in ${formatTime(s.remainingTime)}")
            }
            is MainScreenViewState.TimerPaused -> {
                Text("Stopping in ${formatTime(s.remainingTime)}")
            }
            MainScreenViewState.TimerFinished -> Text("Timer finished")
            else -> {}
        }

    }
}

