package pavball.hr.whitenoise.ui.screens.home

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
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
import pavball.hr.whitenoise.ui.components.NowPlayingCard
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
        .collectAsState(initial = MainScreenViewState())

    var expanded by rememberSaveable { mutableStateOf(false) }
    var fadeEnabled by rememberSaveable { mutableStateOf(true) }
    var chosenMinute by rememberSaveable { mutableStateOf(0) }

    val selectedSoundLabel = state.sounds
        .firstOrNull { it.second == state.currentSound }
        ?.first ?: "Select sound"

    // Dynamic gradient background for NowPlayingCard
    val soundColor = when (state.selectedSoundKey) {
        "ocean" -> listOf(Color(0xFF2196F3), Color(0xFF64B5F6))
        "forest" -> listOf(Color(0xFF4CAF50), Color(0xFF81C784))
        else -> listOf(Color(0xFF3F51B5), Color(0xFF7986CB)) // rain / default
    }

    LaunchedEffect(state.timerSelectedMinutes) {
        chosenMinute = state.timerSelectedMinutes
    }

    // Calculate playback progress
    val progress = remember(state.remainingTime, state.totalTime) {
        if (state.totalTime != null && state.remainingTime != null && state.totalTime!! > 0)
            1f - (state.remainingTime!!.toFloat() / state.totalTime!!.toFloat())
        else 0f
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        NowPlayingCard(
            soundLabel = selectedSoundLabel,
            soundKey = state.currentSound,
            isPlaying = state.isPlaying,
            subLabel = if (state.remainingTime != null)
                "Stopping in ${formatTime(state.remainingTime!!)}"
            else null,
            listOfColors = soundColor
        )

        Spacer(Modifier.height(24.dp))

        // Sound selector
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
                state.sounds.forEach { (label, soundId) ->
                    DropdownMenuItem(
                        modifier = Modifier
                            .height(32.dp)
                            .width(512.dp)
                            .padding(horizontal = 6.dp)
                            .clip(RoundedCornerShape(8.dp)),
                        text = { Text(label, fontWeight = FontWeight.Bold) },
                        onClick = {
                            expanded = false
                            viewModel.updateSelectedSoundKey(selectedSoundKey = soundId)
                            viewModel.playSound(soundId)
                            viewModel.startTimer(
                                if (chosenMinute != 0) chosenMinute else 1,
                                fadeEnabled
                            )
                        }
                    )
                }
            }
        }

        Spacer(Modifier.height(24.dp))

        // Controls with progress ring
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally

        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(24.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Play/Pause with circular progress
                IconButton(
                    onClick = {
                        if (state.isPlaying) {
                            viewModel.pauseSound()
                            viewModel.pauseTimer()
                        } else {
                            viewModel.playSound(state.selectedSoundKey)
                            if (state.remainingTime == null) {
                                viewModel.startTimer(
                                    if (chosenMinute != 0) chosenMinute else 1,
                                    fadeEnabled
                                )
                            } else {
                                viewModel.resumeTimer(
                                    onFadeStart = {},
                                    onTimerFinished = {}
                                )
                            }
                        }
                    }
                ) {
                    Icon(
                        painter = painterResource(
                            if (state.isPlaying) Res.drawable.ic_pause else Res.drawable.ic_play
                        ),
                        contentDescription = if (state.isPlaying) "Pause" else "Play",
                        tint = Color.Blue
                    )
                }

                // Stop
                IconButton(onClick = {
                    viewModel.stopSound()
                    viewModel.cancelTimer()
                }) {
                    Icon(
                        painter = painterResource(Res.drawable.ic_stop),
                        contentDescription = "Stop",
                        tint = Color.Blue
                    )
                }
            }
            Spacer(Modifier.height(32.dp))

            LinearProgressIndicator(
                progress = { progress },
                modifier = Modifier
                    .fillMaxWidth(0.8f)
                    .height(8.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color.LightGray.copy(alpha = 0.4f)),
                color = Color.Blue.copy(alpha = 0.7f),
                trackColor = Color.LightGray.copy(alpha = 0.3f)
            )
        }
        Spacer(Modifier.height(32.dp))

        // Timer selection
        Text("Sleep Timer", style = MaterialTheme.typography.titleMedium)
        Spacer(Modifier.height(8.dp))

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            state.timerOptions.forEach { minutes ->
                Button(
                    onClick = {
                        chosenMinute = minutes
                        viewModel.updateSelectedTimer(minutes)
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (chosenMinute == minutes)
                            Color.Blue.copy(alpha = 0.7f)
                        else
                            Color.Gray.copy(alpha = 0.4f)
                    )
                ) {
                    Text("$minutes min")
                }
            }
        }

        Spacer(Modifier.height(8.dp))
        Button(onClick = { viewModel.cancelTimer() }) { Text("Cancel") }

        Spacer(Modifier.height(12.dp))

        // Fade out option
        Row(verticalAlignment = Alignment.CenterVertically) {
            Checkbox(checked = fadeEnabled, onCheckedChange = { fadeEnabled = it })
            Text("Fade out last 30s")
        }
    }
}
