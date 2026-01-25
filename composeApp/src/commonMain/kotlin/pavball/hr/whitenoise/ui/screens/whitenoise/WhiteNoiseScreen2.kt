package pavball.hr.whitenoise.ui.screens.whitenoise

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
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
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.github.skydoves.colorpicker.compose.rememberColorPickerController
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import pavball.hr.whitenoise.domain.model.rememberSoundPicker
import pavball.hr.whitenoise.ui.components.button.DozzzeButton
import pavball.hr.whitenoise.ui.components.dialog.AddCustomSoundDialog
import pavball.hr.whitenoise.ui.components.effects.WaveformVisualizer
import pavball.hr.whitenoise.ui.components.utils.SpacerHelper
import pavball.hr.whitenoise.ui.screens.main.Screens
import pavball.hr.whitenoise.ui.theme.DarkNavy
import pavball.hr.whitenoise.ui.theme.LightGray
import pavball.hr.whitenoise.ui.theme.Mint
import pavball.hr.whitenoise.ui.theme.getCustomBalooFontFamily
import pavball.hr.whitenoise.ui.theme.getCustomQuicksandFontFamily
import pavball.hr.whitenoise.ui.viewmodels.MainScreenViewModel
import pavball.hr.whitenoise.ui.viewmodels.MainScreenViewState
import whitenoise.composeapp.generated.resources.Res
import whitenoise.composeapp.generated.resources.add_custom_sound
import whitenoise.composeapp.generated.resources.bg_cloudy
import whitenoise.composeapp.generated.resources.fade_out_last
import whitenoise.composeapp.generated.resources.ic_pause
import whitenoise.composeapp.generated.resources.ic_play
import whitenoise.composeapp.generated.resources.ic_skipback
import whitenoise.composeapp.generated.resources.ic_skipnext
import whitenoise.composeapp.generated.resources.sec
import whitenoise.composeapp.generated.resources.select_sound
import whitenoise.composeapp.generated.resources.white_noise

@Composable
internal fun WhiteNoiseScreen2(
    modifier: Modifier = Modifier,
    navController: NavController,
    viewModel: MainScreenViewModel
) {
    val balooFont = getCustomBalooFontFamily()
    val quickSandFont = getCustomQuicksandFontFamily()

    // Collect View State
    val state by viewModel.viewState<MainScreenViewState>()
        .collectAsState(initial = MainScreenViewState())
    val customSounds = state.customSounds

    // State for UI Logic
    var expanded by rememberSaveable { mutableStateOf(false) }

    // Combine sounds for the list
    val combinedSounds = remember(state.sounds, customSounds) {
        state.sounds + customSounds.map { it.displayName to it.id }
    }

    val selectedSoundLabel = combinedSounds.firstOrNull { it.second == state.currentSound }?.first
        ?: stringResource(Res.string.select_sound)


    Box(modifier = Modifier.fillMaxSize()) {
        // 1. Background Layer
        Image(
            painter = painterResource(Res.drawable.bg_cloudy),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )

        // 2. Dark Overlay
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.6f))
        )

        // 3. Main Content
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            SpacerHelper(60.dp) // Top Margin

            // --- Header ---
            Text(
                text = stringResource(Res.string.white_noise),
                style = MaterialTheme.typography.headlineLarge,
                fontFamily = balooFont,
                fontWeight = FontWeight.Normal,
                color = Mint
            )

            Spacer(modifier = Modifier.weight(1f)) // Push content to center

            // --- Visualizer ---
            WaveformVisualizer(
                isPlaying = state.isPlaying,
                soundType = state.currentSound,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(80.dp)
            )

            Spacer(modifier = Modifier.weight(1f))

            // --- Buttons Row (Choose Sound / Add Sound) ---
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                // Button 1: Choose Sound (With Dropdown)

                Box(modifier = Modifier.weight(1f)) {
                    DozzzeButton(
                        modifier = Modifier.height(60.dp),
                        buttonText = stringResource(Res.string.select_sound),
                        buttonTextStyle = MaterialTheme.typography.bodySmall,
                        buttonTextFont = quickSandFont,
                        buttonTextWeight = FontWeight.Medium,
                        buttonWidth = 150.dp,
                        shape = RoundedCornerShape(20.dp),
                        backgroundColor = Color.White.copy(alpha = 0.9f),
                        textColor = Color(0xFF1B2236),
                        onClicked = { expanded = true }
                    )

                    // Dropdown Logic
                    DropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false },
                        modifier = Modifier.background(Color.White)
                    ) {
                        combinedSounds.forEach { (label, id) ->
                            DropdownMenuItem(
                                text = {
                                    Text(
                                        text = label,
                                        fontFamily = quickSandFont, // Fixed missing font
                                        color = DarkNavy
                                    )
                                },
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


                DozzzeButton(
                    modifier = Modifier.height(60.dp),
                    buttonText = stringResource(Res.string.add_custom_sound),
                    buttonTextStyle = MaterialTheme.typography.bodySmall,
                    buttonTextFont = quickSandFont,
                    buttonTextWeight = FontWeight.Medium,
                    buttonWidth = 150.dp,
                    shape = RoundedCornerShape(20.dp),
                    onClicked = { navController.navigate(Screens.AddSound.route) }
                )
            }

            SpacerHelper(40.dp)

            // --- Timer Section ---
            Text(
                text = "Sound timer",
                fontFamily = quickSandFont,
                color = Color.White.copy(alpha = 0.7f),
                fontSize = 14.sp
            )

            SpacerHelper(10.dp)

            // Horizontal Timer Picker
            LazyRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                items(state.timerOptions) { minutes ->
                    val isSelected = state.timerSelectedMinutes == minutes

                    Text(
                        text = minutes.toString(),
                        fontFamily = quickSandFont,
                        color = if (isSelected) Color.White else Color.White.copy(alpha = 0.5f),
                        fontSize = if (isSelected) 24.sp else 18.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                        modifier = Modifier
                            .padding(horizontal = 12.dp)
                            .clickable { viewModel.updateSelectedTimer(minutes) }
                    )
                }
            }

            Text(
                text = "min",
                fontFamily = quickSandFont,
                color = Color.White.copy(alpha = 0.7f),
                fontSize = 16.sp,
                modifier = Modifier.padding(top = 4.dp)
            )

            SpacerHelper(20.dp)

            // --- Fade Checkbox ---
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Checkbox(
                    checked = state.fadeEnabled,
                    onCheckedChange = { viewModel.updateFadeEnabled(it) },
                    colors = CheckboxDefaults.colors(
                        checkedColor = DarkNavy,
                        checkmarkColor = if (state.fadeEnabled) LightGray else Color.Transparent,
                        uncheckedColor = Color.White.copy(alpha = 0.5f)
                    )
                )
                Text(
                    text = "${stringResource(Res.string.fade_out_last)} ${state.fadeDuration} ${
                        stringResource(
                            Res.string.sec
                        )
                    }",
                    fontFamily = quickSandFont,
                    color = Color.White.copy(alpha = 0.8f),
                    fontSize = 14.sp
                )
            }

            SpacerHelper(20.dp)

            // --- Player Controls (Prev | Play | Next) ---
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Previous Button
                IconButton(
                    onClick = {
                        val currentIndex =
                            combinedSounds.indexOfFirst { it.second == state.currentSound }
                        if (currentIndex != -1) {
                            val prevIndex =
                                if (currentIndex - 1 < 0) combinedSounds.lastIndex else currentIndex - 1
                            val prevId = combinedSounds[prevIndex].second
                            viewModel.updateSelectedSoundKey(prevId)
                            viewModel.playSound(prevId)
                        }
                    },
                    modifier = Modifier.size(48.dp)
                ) {
                    Icon(
                        painter = painterResource(Res.drawable.ic_skipback),
                        contentDescription = "Previous",
                        tint = Color.White,
                        modifier = Modifier.size(36.dp)
                    )
                }

                // Play/Pause Button
                IconButton(
                    onClick = {
                        if (state.isPlaying) {
                            viewModel.pauseSound()
                            viewModel.pauseTimer()
                        } else {
                            viewModel.playSound(state.selectedSoundKey)
                            if (state.remainingTime == null)
                                viewModel.startTimer(
                                    if (state.timerSelectedMinutes != 0) state.timerSelectedMinutes else 1,
                                    state.fadeEnabled
                                )
                            else viewModel.resumeTimer(onFadeStart = {}, onTimerFinished = {})
                        }
                    },
                    modifier = Modifier.size(64.dp)
                ) {
                    Icon(
                        painter = painterResource(if (state.isPlaying) Res.drawable.ic_pause else Res.drawable.ic_play),
                        contentDescription = "Play/Pause",
                        tint = Color.White,
                        modifier = Modifier.size(48.dp)
                    )
                }

                // Next Button
                IconButton(
                    onClick = {
                        val currentIndex =
                            combinedSounds.indexOfFirst { it.second == state.currentSound }
                        if (currentIndex != -1) {
                            val nextIndex = (currentIndex + 1) % combinedSounds.size
                            val nextId = combinedSounds[nextIndex].second
                            viewModel.updateSelectedSoundKey(nextId)
                            viewModel.playSound(nextId)
                        }
                    },
                    modifier = Modifier.size(48.dp)
                ) {
                    Icon(
                        painter = painterResource(Res.drawable.ic_skipnext),
                        contentDescription = "Next",
                        tint = Color.White,
                        modifier = Modifier.size(36.dp)
                    )
                }
            }

            SpacerHelper(40.dp) // Bottom padding
        }
    }


}
