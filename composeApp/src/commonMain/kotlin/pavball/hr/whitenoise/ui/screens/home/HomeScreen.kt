package pavball.hr.whitenoise.ui.screens.home

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
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
import org.jetbrains.compose.resources.painterResource
import pavball.hr.whitenoise.domain.model.rememberSoundPicker
import pavball.hr.whitenoise.ui.components.NowPlayingCard
import pavball.hr.whitenoise.ui.components.RenameSoundDialog
import pavball.hr.whitenoise.ui.components.formatTime
import pavball.hr.whitenoise.ui.viewmodels.MainScreenViewModel
import pavball.hr.whitenoise.ui.viewmodels.MainScreenViewState
import whitenoise.composeapp.generated.resources.Res
import whitenoise.composeapp.generated.resources.ic_pause
import whitenoise.composeapp.generated.resources.ic_play
import whitenoise.composeapp.generated.resources.ic_stop

@Composable
internal fun HomeScreen(
    modifier: Modifier = Modifier,
    viewModel: MainScreenViewModel
) {
    val state by viewModel.viewState<MainScreenViewState>()
        .collectAsState(initial = MainScreenViewState())

    var expanded by rememberSaveable { mutableStateOf(false) }

    val customSounds by viewModel.userSounds.collectAsState()

    val combinedSounds = remember(state.sounds, customSounds) {
        state.sounds + customSounds.map { it.displayName to it.id }
    }

    val selectedSoundLabel =
        combinedSounds.firstOrNull { it.second == state.currentSound }?.first ?: "Select sound"
    val pendingRename by viewModel.pendingRename.collectAsState()

    val launchSoundPicker = rememberSoundPicker { picked ->
        if (picked != null) {
            // add saved sound and show rename dialog
            viewModel.addUserSound(picked.displayName, picked.uri)
        }
    }

    val progress = remember(state.remainingTime, state.totalTime) {
        if (state.totalTime != null && state.remainingTime != null && state.totalTime!! > 0)
            1f - (state.remainingTime!!.toFloat() / state.totalTime!!.toFloat())
        else 0f
    }

    Column(
        modifier = modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        NowPlayingCard(
            soundLabel = if (!state.isCleared) (selectedSoundLabel) else ("Select sound"),
            soundKey = state.currentSound,
            isPlaying = state.isPlaying,
            subLabel = if (state.remainingTime != null) "Stopping in ${formatTime(state.remainingTime!!)}" else null,
            listOfColors = when (state.selectedSoundKey) {
                "ocean" -> listOf(Color(0xFF2196F3), Color(0xFF64B5F6))
                "forest" -> listOf(Color(0xFF4CAF50), Color(0xFF81C784))
                else -> listOf(Color(0xFF3F51B5), Color(0xFF7986CB))
            }
        )

        Spacer(Modifier.height(24.dp))

        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Button(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp),
                onClick = { expanded = !expanded },
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.Blue.copy(alpha = 0.5f),
                    contentColor = Color.White
                ),
                shape = RoundedCornerShape(12.dp),
                border = BorderStroke(1.dp, Color.Black.copy(alpha = 0.75f))
            ) {
                Text(selectedSoundLabel)
                DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                    combinedSounds.forEach { (label, id) ->
                        DropdownMenuItem(
                            text = { Text(label, fontWeight = FontWeight.Bold) },
                            onClick = {
                                expanded = false
                                viewModel.updateSelectedSoundKey(id)
                                viewModel.playSound(id)
                                viewModel.startTimer(
                                    if (state.timerSelectedMinutes != 0) state.timerSelectedMinutes else 1,
                                    state.fadeEnabled
                                )
                            }
                        )
                    }
                }
            }

            Spacer(Modifier.height(8.dp))

            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Button(
                    onClick = { launchSoundPicker() },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.Blue.copy(alpha = 0.5f),
                        contentColor = Color.White
                    )
                ) {
                    Text("Add Custom Sound")
                }
            }
        }

        Spacer(Modifier.height(24.dp))

        Row(
            horizontalArrangement = Arrangement.spacedBy(24.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = {
                if (state.isPlaying) {
                    viewModel.pauseSound(); viewModel.pauseTimer()
                } else {
                    viewModel.playSound(state.selectedSoundKey)
                    if (state.remainingTime == null) viewModel.startTimer(
                        if (state.timerSelectedMinutes != 0) state.timerSelectedMinutes else 1,
                        state.fadeEnabled
                    )
                    else viewModel.resumeTimer(onFadeStart = {}, onTimerFinished = {})
                }
            }) {
                Icon(
                    painterResource(if (state.isPlaying) Res.drawable.ic_pause else Res.drawable.ic_play),
                    contentDescription = null,
                    tint = Color.Blue
                )
            }

            IconButton(onClick = { viewModel.stopSound(); viewModel.cancelTimer() }) {
                Icon(
                    painterResource(Res.drawable.ic_stop),
                    contentDescription = null,
                    tint = Color.Blue
                )
            }
        }

        Spacer(Modifier.height(24.dp))

        LinearProgressIndicator(
            progress = { progress },
            modifier = Modifier.fillMaxWidth(0.8f).height(8.dp).clip(RoundedCornerShape(12.dp)),
            color = Color.Blue.copy(alpha = 0.7f),
            trackColor = Color.LightGray.copy(alpha = 0.3f)
        )

        Spacer(Modifier.height(24.dp))

        Text("Sleep Timer", style = MaterialTheme.typography.titleMedium)
        Spacer(Modifier.height(8.dp))
        LazyVerticalGrid(
            columns = GridCells.Fixed(3),
            modifier = Modifier
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            contentPadding = PaddingValues(horizontal = 8.dp)
        ) {
            items(state.timerOptions) { minutes ->
                Button(
                    onClick = { viewModel.updateSelectedTimer(minutes) },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (state.timerSelectedMinutes == minutes)
                            Color.Blue.copy(alpha = 0.7f)
                        else Color.Gray.copy(alpha = 0.4f),
                        contentColor = Color.White
                    )
                ) {
                    Text("$minutes min")
                }
            }
        }
        Spacer(Modifier.height(8.dp))
        Button(onClick = { viewModel.cancelTimer() }) { Text("Cancel") }
        Spacer(Modifier.height(12.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
            Checkbox(
                checked = state.fadeEnabled,
                onCheckedChange = { viewModel.updateFadeEnabled(it) }
            )
            Text("Fade out last 30s")
        }
    }

    // Show rename dialog when pendingRename is set by the ViewModel
    pendingRename?.let { cs ->
        RenameSoundDialog(
            currentName = cs.displayName,
            onDismiss = { viewModel.clearPendingRename() },
            onRename = { newName ->
                viewModel.renameCustomSound(cs.id, newName)
            }
        )
    }
}
