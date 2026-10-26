package pavball.hr.whitenoise.ui.screens.whitenoise

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import pavball.hr.whitenoise.ui.components.button.DozzzeButton
import pavball.hr.whitenoise.ui.components.effects.WaveformVisualizer
import pavball.hr.whitenoise.ui.components.utils.SpacerHelper
import pavball.hr.whitenoise.ui.components.utils.formatClock
import pavball.hr.whitenoise.ui.components.utils.formatTime
import pavball.hr.whitenoise.ui.components.utils.fromHex
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
import whitenoise.composeapp.generated.resources.cancel_timer
import whitenoise.composeapp.generated.resources.extend_timer
import whitenoise.composeapp.generated.resources.fade_out_last
import whitenoise.composeapp.generated.resources.ic_pause
import whitenoise.composeapp.generated.resources.ic_play
import whitenoise.composeapp.generated.resources.ic_skipback
import whitenoise.composeapp.generated.resources.ic_skipnext
import whitenoise.composeapp.generated.resources.loading
import whitenoise.composeapp.generated.resources.min
import whitenoise.composeapp.generated.resources.next
import whitenoise.composeapp.generated.resources.now_playing
import whitenoise.composeapp.generated.resources.pause
import whitenoise.composeapp.generated.resources.paused
import whitenoise.composeapp.generated.resources.play
import whitenoise.composeapp.generated.resources.playback_error
import whitenoise.composeapp.generated.resources.previous
import whitenoise.composeapp.generated.resources.sec
import whitenoise.composeapp.generated.resources.select_sound
import whitenoise.composeapp.generated.resources.sleep_timer
import whitenoise.composeapp.generated.resources.stops_at
import whitenoise.composeapp.generated.resources.timer_fading_out
import whitenoise.composeapp.generated.resources.timer_hint_off
import whitenoise.composeapp.generated.resources.timer_hint_pick_time
import whitenoise.composeapp.generated.resources.timer_hint_press_play
import whitenoise.composeapp.generated.resources.timer_off
import whitenoise.composeapp.generated.resources.timer_paused
import whitenoise.composeapp.generated.resources.white_noise
import kotlin.time.Clock
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.ExperimentalTime

@Composable
internal fun WhiteNoiseScreen(
    modifier: Modifier = Modifier,
    navController: NavController,
    viewModel: MainScreenViewModel
) {
    val balooFont = getCustomBalooFontFamily()
    val quickSandFont = getCustomQuicksandFontFamily()

    // Collect View State
    val state by viewModel.viewState<MainScreenViewState>()
        .collectAsState(initial = MainScreenViewState())

    val customColor = state.selectedCustomSound?.colorId?.let { Color.fromHex(it) }

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
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            SpacerHelper(48.dp) // Top Margin

            // --- Header ---
            Text(
                text = stringResource(Res.string.white_noise),
                style = MaterialTheme.typography.headlineLarge,
                fontFamily = balooFont,
                fontWeight = FontWeight.Normal,
                color = Mint
            )

            SpacerHelper(28.dp)

            // --- Now playing ---
            Text(
                text = when {
                    state.isLoading -> stringResource(Res.string.loading)
                    state.isPlaying -> stringResource(Res.string.now_playing)
                    else -> stringResource(Res.string.paused)
                },
                fontFamily = quickSandFont,
                color = Color.White.copy(alpha = 0.6f),
                fontSize = 14.sp
            )
            Text(
                text = state.selectedSoundLabel ?: stringResource(Res.string.select_sound),
                fontFamily = quickSandFont,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                fontSize = 24.sp,
                textAlign = TextAlign.Center
            )

            SpacerHelper(16.dp)

            // --- Visualizer ---
            WaveformVisualizer(
                isPlaying = state.isPlaying,
                soundType = state.selectedSoundKey,
                customColor = customColor,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(80.dp)
            )

            if (state.playbackError) {
                SpacerHelper(8.dp)
                Text(
                    text = stringResource(Res.string.playback_error),
                    fontFamily = quickSandFont,
                    color = Color(0xFFFF8A80),
                    fontSize = 14.sp,
                    textAlign = TextAlign.Center
                )
            }

            SpacerHelper(28.dp)

            // --- Buttons Row (Choose Sound / Add Sound) ---
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
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
                    onClicked = { navController.navigate(Screens.SelectSound.route) }
                )

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

            SpacerHelper(32.dp)

            // --- Timer Section ---
            Text(
                text = stringResource(Res.string.sleep_timer),
                fontFamily = quickSandFont,
                color = Color.White.copy(alpha = 0.7f),
                fontSize = 14.sp
            )

            SpacerHelper(8.dp)

            TimerStatus(state = state, font = quickSandFont, viewModel = viewModel)

            SpacerHelper(16.dp)

            // Horizontal Timer Picker
            LazyRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(4.dp, Alignment.CenterHorizontally),
                verticalAlignment = Alignment.CenterVertically,
                contentPadding = PaddingValues(horizontal = 8.dp)
            ) {
                items(state.timerOptions) { minutes ->
                    val isSelected = state.timerSelectedMinutes == minutes

                    TextButton(onClick = { viewModel.updateSelectedTimer(minutes) }) {
                        Text(
                            text = if (minutes == 0) stringResource(Res.string.timer_off) else minutes.toString(),
                            fontFamily = quickSandFont,
                            color = if (isSelected) Color.White else Color.White.copy(alpha = 0.5f),
                            fontSize = if (isSelected) 24.sp else 18.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                        )
                    }
                }
            }

            Text(
                text = stringResource(Res.string.min),
                fontFamily = quickSandFont,
                color = Color.White.copy(alpha = 0.7f),
                fontSize = 16.sp
            )

            SpacerHelper(12.dp)

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

            SpacerHelper(16.dp)

            // --- Player Controls (Prev | Play | Next) ---
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = { viewModel.skipToPrevious() },
                    modifier = Modifier.size(48.dp)
                ) {
                    Icon(
                        painter = painterResource(Res.drawable.ic_skipback),
                        contentDescription = stringResource(Res.string.previous),
                        tint = Color.White,
                        modifier = Modifier.size(36.dp)
                    )
                }

                IconButton(
                    onClick = { viewModel.togglePlayPause() },
                    modifier = Modifier.size(64.dp)
                ) {
                    Icon(
                        painter = painterResource(if (state.isPlaying) Res.drawable.ic_pause else Res.drawable.ic_play),
                        contentDescription = stringResource(if (state.isPlaying) Res.string.pause else Res.string.play),
                        tint = Color.White,
                        modifier = Modifier.size(48.dp)
                    )
                }

                IconButton(
                    onClick = { viewModel.skipToNext() },
                    modifier = Modifier.size(48.dp)
                ) {
                    Icon(
                        painter = painterResource(Res.drawable.ic_skipnext),
                        contentDescription = stringResource(Res.string.next),
                        tint = Color.White,
                        modifier = Modifier.size(36.dp)
                    )
                }
            }

            SpacerHelper(40.dp) // Bottom padding
        }
    }
}

/** Countdown with "stops at" time and quick actions, or a hint when no timer runs. */
@OptIn(ExperimentalTime::class)
@Composable
private fun TimerStatus(
    state: MainScreenViewState,
    font: FontFamily,
    viewModel: MainScreenViewModel
) {
    val remaining = state.remainingTime
    val total = state.totalTime

    Column(
        modifier = Modifier.fillMaxWidth().heightIn(min = 120.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        if (remaining == null || total == null) {
            Text(
                text = stringResource(
                    when {
                        state.timerSelectedMinutes == 0 -> Res.string.timer_hint_off
                        state.isPlaying -> Res.string.timer_hint_pick_time
                        else -> Res.string.timer_hint_press_play
                    }
                ),
                fontFamily = font,
                color = Color.White.copy(alpha = 0.8f),
                fontSize = 16.sp,
                textAlign = TextAlign.Center
            )
            return@Column
        }

        Text(
            text = formatTime(remaining),
            fontFamily = font,
            fontWeight = FontWeight.SemiBold,
            color = LightGray,
            fontSize = 44.sp
        )

        val status = when {
            state.fadeStarted -> stringResource(Res.string.timer_fading_out)
            !state.isTimerRunning -> stringResource(Res.string.timer_paused)
            else -> {
                val stopsAt = (Clock.System.now() + remaining.milliseconds)
                    .toLocalDateTime(TimeZone.currentSystemDefault())
                "${stringResource(Res.string.stops_at)} ${formatClock(stopsAt.hour, stopsAt.minute)}"
            }
        }
        Text(
            text = status,
            fontFamily = font,
            color = Color.White.copy(alpha = 0.7f),
            fontSize = 14.sp
        )

        SpacerHelper(8.dp)

        LinearProgressIndicator(
            progress = { if (total > 0) 1f - remaining.toFloat() / total else 0f },
            modifier = Modifier
                .fillMaxWidth(0.7f)
                .height(6.dp)
                .clip(RoundedCornerShape(12.dp)),
            color = Mint,
            trackColor = Color.White.copy(alpha = 0.2f)
        )

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            TextButton(onClick = { viewModel.extendTimer(5) }) {
                Text(stringResource(Res.string.extend_timer), fontFamily = font, color = Mint)
            }
            TextButton(onClick = { viewModel.cancelTimer() }) {
                Text(stringResource(Res.string.cancel_timer), fontFamily = font, color = Color.White.copy(alpha = 0.7f))
            }
        }
    }
}
