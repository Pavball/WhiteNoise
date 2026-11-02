package pavball.hr.whitenoise.ui.screens.whitenoise

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import kotlinx.coroutines.delay
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import pavball.hr.whitenoise.ui.components.button.DozzzePlayButton
import pavball.hr.whitenoise.ui.components.effects.DozzzeGlassCard
import pavball.hr.whitenoise.ui.components.effects.SpotlightOverlay
import pavball.hr.whitenoise.ui.components.effects.SpotlightStep
import pavball.hr.whitenoise.ui.components.effects.WaveformVisualizer
import pavball.hr.whitenoise.ui.components.effects.rememberSpotlightState
import pavball.hr.whitenoise.ui.components.effects.spotlightTarget
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
import whitenoise.composeapp.generated.resources.add
import whitenoise.composeapp.generated.resources.bg_cloudy
import whitenoise.composeapp.generated.resources.cancel_timer
import whitenoise.composeapp.generated.resources.extend_timer
import whitenoise.composeapp.generated.resources.fade_out_last
import whitenoise.composeapp.generated.resources.ic_skipback
import whitenoise.composeapp.generated.resources.ic_skipnext
import whitenoise.composeapp.generated.resources.loading
import whitenoise.composeapp.generated.resources.minutes_short
import whitenoise.composeapp.generated.resources.mixer
import whitenoise.composeapp.generated.resources.next
import whitenoise.composeapp.generated.resources.now_playing
import whitenoise.composeapp.generated.resources.paused
import whitenoise.composeapp.generated.resources.playback_error
import whitenoise.composeapp.generated.resources.playing_with
import whitenoise.composeapp.generated.resources.previous
import whitenoise.composeapp.generated.resources.sec
import whitenoise.composeapp.generated.resources.select_sound
import whitenoise.composeapp.generated.resources.sleep_timer
import whitenoise.composeapp.generated.resources.sounds
import whitenoise.composeapp.generated.resources.stops_at
import whitenoise.composeapp.generated.resources.timer_fading_out
import whitenoise.composeapp.generated.resources.timer_hint_off
import whitenoise.composeapp.generated.resources.timer_hint_pick_time
import whitenoise.composeapp.generated.resources.timer_hint_press_play
import whitenoise.composeapp.generated.resources.timer_off
import whitenoise.composeapp.generated.resources.timer_paused
import whitenoise.composeapp.generated.resources.tip_controls_text
import whitenoise.composeapp.generated.resources.tip_controls_title
import whitenoise.composeapp.generated.resources.tip_mixer_text
import whitenoise.composeapp.generated.resources.tip_mixer_title
import whitenoise.composeapp.generated.resources.tip_sounds_text
import whitenoise.composeapp.generated.resources.tip_sounds_title
import whitenoise.composeapp.generated.resources.tip_timer_text
import whitenoise.composeapp.generated.resources.tip_timer_title
import whitenoise.composeapp.generated.resources.tip_visualizer_text
import whitenoise.composeapp.generated.resources.tip_visualizer_title
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
    // Read only while drawing the visualizer, so it redraws without recomposing the screen
    val spectrum by viewModel.spectrum.collectAsState(initial = FloatArray(0))

    val customColor = state.selectedCustomSound?.colorId?.let { Color.fromHex(it) }

    // --- First-visit tips ---
    val scrollState = rememberScrollState()
    val spotlight = rememberSpotlightState()
    val tips = whiteNoiseTips()
    var showTips by remember { mutableStateOf(false) }
    var tipIndex by remember { mutableIntStateOf(0) }
    val finishTips = {
        showTips = false
        viewModel.completeWhiteNoiseTips()
    }
    LaunchedEffect(state.whiteNoiseTipsSeen) {
        if (state.whiteNoiseTipsSeen == false) {
            delay(TIPS_DELAY_MS) // let the screen settle first
            showTips = true
        }
    }
    LaunchedEffect(showTips, tipIndex) {
        // The timer sits below the fold; scroll it into view for its tip.
        if (showTips) {
            scrollState.animateScrollTo(if (tips[tipIndex].targetKey == TIP_TIMER) scrollState.maxValue else 0)
        }
    }

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
                .verticalScroll(scrollState)
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            SpacerHelper(40.dp) // Top Margin

            // --- Header ---
            Text(
                text = stringResource(Res.string.white_noise),
                style = MaterialTheme.typography.headlineLarge,
                fontFamily = balooFont,
                fontWeight = FontWeight.Normal,
                color = Mint
            )

            SpacerHelper(24.dp)

            // --- Now playing ---
            Text(
                text = when {
                    state.isLoading -> stringResource(Res.string.loading)
                    state.isPlaying -> stringResource(Res.string.now_playing)
                    else -> stringResource(Res.string.paused)
                }.uppercase(),
                fontFamily = quickSandFont,
                fontWeight = FontWeight.SemiBold,
                color = Color.White.copy(alpha = 0.55f),
                fontSize = 12.sp,
                letterSpacing = 2.sp
            )
            SpacerHelper(4.dp)
            Text(
                text = state.selectedSoundLabel ?: stringResource(Res.string.select_sound),
                fontFamily = quickSandFont,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                fontSize = 26.sp,
                textAlign = TextAlign.Center
            )

            val layerNames = state.activeLayers.keys.mapNotNull(state::labelFor)
            if (layerNames.isNotEmpty()) {
                Text(
                    text = stringResource(Res.string.playing_with, layerNames.joinToString(", ")),
                    fontFamily = quickSandFont,
                    fontWeight = FontWeight.Medium,
                    color = Mint,
                    fontSize = 14.sp,
                    textAlign = TextAlign.Center,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier
                        .padding(top = 4.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .clickable { navController.navigate(Screens.Mixer.route) }
                        .padding(horizontal = 8.dp, vertical = 2.dp)
                )
            }

            SpacerHelper(20.dp)

            // --- Visualizer ---
            WaveformVisualizer(
                isPlaying = state.isPlaying,
                soundType = state.selectedSoundKey,
                customColor = customColor,
                levels = { spectrum },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(96.dp)
                    .spotlightTarget(spotlight, TIP_VISUALIZER)
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

            SpacerHelper(20.dp)

            // --- Player Controls (Prev | Play | Next) ---
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .spotlightTarget(spotlight, TIP_CONTROLS),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = { viewModel.skipToPrevious() },
                    modifier = Modifier.size(56.dp)
                ) {
                    Icon(
                        painter = painterResource(Res.drawable.ic_skipback),
                        contentDescription = stringResource(Res.string.previous),
                        tint = Color.White,
                        modifier = Modifier.size(32.dp)
                    )
                }

                DozzzePlayButton(
                    isPlaying = state.isPlaying,
                    onClick = { viewModel.togglePlayPause() }
                )

                IconButton(
                    onClick = { viewModel.skipToNext() },
                    modifier = Modifier.size(56.dp)
                ) {
                    Icon(
                        painter = painterResource(Res.drawable.ic_skipnext),
                        contentDescription = stringResource(Res.string.next),
                        tint = Color.White,
                        modifier = Modifier.size(32.dp)
                    )
                }
            }

            SpacerHelper(28.dp)

            // --- Sounds / Mixer / Add ---
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                ActionPill(
                    text = stringResource(Res.string.sounds),
                    font = quickSandFont,
                    onClick = { navController.navigate(Screens.SelectSound.route) },
                    modifier = Modifier.weight(1f).spotlightTarget(spotlight, TIP_SOUNDS)
                )
                ActionPill(
                    text = stringResource(Res.string.mixer),
                    font = quickSandFont,
                    highlighted = true,
                    onClick = { navController.navigate(Screens.Mixer.route) },
                    modifier = Modifier.weight(1f).spotlightTarget(spotlight, TIP_MIXER)
                )
                ActionPill(
                    text = stringResource(Res.string.add),
                    font = quickSandFont,
                    onClick = { navController.navigate(Screens.AddSound.route) },
                    modifier = Modifier.weight(1f)
                )
            }

            SpacerHelper(24.dp)

            // --- Timer Section ---
            DozzzeGlassCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .spotlightTarget(spotlight, TIP_TIMER),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = stringResource(Res.string.sleep_timer).uppercase(),
                    fontFamily = quickSandFont,
                    fontWeight = FontWeight.SemiBold,
                    color = Color.White.copy(alpha = 0.55f),
                    fontSize = 12.sp,
                    letterSpacing = 2.sp
                )

                TimerStatus(state = state, font = quickSandFont, viewModel = viewModel)

                TimerPicker(state = state, font = quickSandFont, onPick = viewModel::updateSelectedTimer)

                SpacerHelper(8.dp)

                // --- Fade switch ---
                Row(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "${stringResource(Res.string.fade_out_last)} ${state.fadeDuration} ${
                            stringResource(Res.string.sec)
                        }",
                        fontFamily = quickSandFont,
                        color = Color.White.copy(alpha = 0.8f),
                        fontSize = 14.sp,
                        modifier = Modifier.weight(1f)
                    )
                    Switch(
                        checked = state.fadeEnabled,
                        onCheckedChange = { viewModel.updateFadeEnabled(it) },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = DarkNavy,
                            checkedTrackColor = Mint,
                            uncheckedThumbColor = Color.White.copy(alpha = 0.7f),
                            uncheckedTrackColor = Color.White.copy(alpha = 0.12f),
                            uncheckedBorderColor = Color.White.copy(alpha = 0.3f)
                        )
                    )
                }
            }

            SpacerHelper(32.dp) // Bottom padding
        }

        if (showTips) {
            SpotlightOverlay(
                state = spotlight,
                steps = tips,
                currentStep = tipIndex,
                onNext = { if (tipIndex < tips.lastIndex) tipIndex++ else finishTips() },
                onSkip = finishTips,
                font = quickSandFont
            )
        }
    }
}

private const val TIPS_DELAY_MS = 600L
private const val TIP_VISUALIZER = "visualizer"
private const val TIP_CONTROLS = "controls"
private const val TIP_MIXER = "mixer"
private const val TIP_SOUNDS = "sounds"
private const val TIP_TIMER = "timer"

@Composable
private fun whiteNoiseTips(): List<SpotlightStep> = listOf(
    SpotlightStep(TIP_VISUALIZER, stringResource(Res.string.tip_visualizer_title), stringResource(Res.string.tip_visualizer_text)),
    SpotlightStep(TIP_CONTROLS, stringResource(Res.string.tip_controls_title), stringResource(Res.string.tip_controls_text)),
    SpotlightStep(TIP_MIXER, stringResource(Res.string.tip_mixer_title), stringResource(Res.string.tip_mixer_text)),
    SpotlightStep(TIP_SOUNDS, stringResource(Res.string.tip_sounds_title), stringResource(Res.string.tip_sounds_text)),
    SpotlightStep(TIP_TIMER, stringResource(Res.string.tip_timer_title), stringResource(Res.string.tip_timer_text))
)

@Composable
private fun ActionPill(
    text: String,
    font: FontFamily,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    highlighted: Boolean = false
) {
    val shape = RoundedCornerShape(18.dp)
    Box(
        modifier = modifier
            .height(52.dp)
            .clip(shape)
            .background(if (highlighted) Mint else Color.White.copy(alpha = 0.12f))
            .border(1.dp, if (highlighted) Mint else Color.White.copy(alpha = 0.15f), shape)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            fontFamily = font,
            fontWeight = FontWeight.SemiBold,
            fontSize = 15.sp,
            color = if (highlighted) DarkNavy else Color.White,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.padding(horizontal = 8.dp)
        )
    }
}

/** Horizontal row of timer durations; scrolls so the chosen one is in view. */
@Composable
private fun TimerPicker(
    state: MainScreenViewState,
    font: FontFamily,
    onPick: (Int) -> Unit
) {
    val listState = rememberLazyListState(
        initialFirstVisibleItemIndex = (state.timerOptions.indexOf(state.timerSelectedMinutes) - 1).coerceAtLeast(0)
    )
    LazyRow(
        state = listState,
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        contentPadding = PaddingValues(horizontal = 4.dp)
    ) {
        items(state.timerOptions) { minutes ->
            val isSelected = state.timerSelectedMinutes == minutes
            val shape = RoundedCornerShape(50)
            Text(
                text = if (minutes == 0) stringResource(Res.string.timer_off)
                else stringResource(Res.string.minutes_short, minutes),
                fontFamily = font,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                fontSize = 14.sp,
                color = if (isSelected) DarkNavy else Color.White.copy(alpha = 0.85f),
                modifier = Modifier
                    .clip(shape)
                    .background(if (isSelected) Mint else Color.White.copy(alpha = 0.06f))
                    .border(1.dp, if (isSelected) Mint else Color.White.copy(alpha = 0.15f), shape)
                    .clickable { onPick(minutes) }
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            )
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
        modifier = Modifier.fillMaxWidth().heightIn(min = 96.dp),
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
