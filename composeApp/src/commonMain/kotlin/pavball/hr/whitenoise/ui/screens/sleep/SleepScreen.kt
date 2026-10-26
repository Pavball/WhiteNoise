package pavball.hr.whitenoise.ui.screens.sleep

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import pavball.hr.whitenoise.ui.components.button.DozzzeButton
import pavball.hr.whitenoise.ui.components.effects.GlowingTimerCircle
import pavball.hr.whitenoise.ui.components.input.RatingBar
import pavball.hr.whitenoise.ui.components.input.WheelPicker
import pavball.hr.whitenoise.ui.components.utils.SpacerHelper
import pavball.hr.whitenoise.ui.components.utils.formatClock
import pavball.hr.whitenoise.ui.components.utils.formatDuration
import pavball.hr.whitenoise.ui.components.utils.formatTime
import pavball.hr.whitenoise.ui.theme.DarkNavy
import pavball.hr.whitenoise.ui.theme.LightGray
import pavball.hr.whitenoise.ui.theme.Mint
import pavball.hr.whitenoise.ui.theme.getCustomBalooFontFamily
import pavball.hr.whitenoise.ui.theme.getCustomQuicksandFontFamily
import pavball.hr.whitenoise.ui.viewmodels.MainScreenViewModel
import pavball.hr.whitenoise.ui.viewmodels.MainScreenViewState
import pavball.hr.whitenoise.ui.viewmodels.SleepViewModel
import pavball.hr.whitenoise.ui.viewmodels.SleepViewState
import whitenoise.composeapp.generated.resources.Res
import whitenoise.composeapp.generated.resources.alarm_failed
import whitenoise.composeapp.generated.resources.alarm_label
import whitenoise.composeapp.generated.resources.alarm_set_for
import whitenoise.composeapp.generated.resources.bg_art
import whitenoise.composeapp.generated.resources.how_did_you_sleep
import whitenoise.composeapp.generated.resources.not_sleeping_yet
import whitenoise.composeapp.generated.resources.set_alarm_start
import whitenoise.composeapp.generated.resources.skip
import whitenoise.composeapp.generated.resources.sleep
import whitenoise.composeapp.generated.resources.sleeping_for
import whitenoise.composeapp.generated.resources.until_wake_up
import whitenoise.composeapp.generated.resources.wake_up
import whitenoise.composeapp.generated.resources.when_wake_up
import whitenoise.composeapp.generated.resources.you_slept

/**
 * Bedtime: pick a wake-up time, hand the alarm to the Clock app and start the
 * white noise (with its sleep timer). Waking up ends the tracked night.
 */
@Composable
internal fun SleepScreen(
    modifier: Modifier = Modifier,
    playerViewModel: MainScreenViewModel,
    viewModel: SleepViewModel = koinViewModel()
) {

    val balooFont = getCustomBalooFontFamily()
    val quickSandFont = getCustomQuicksandFontFamily()

    val state by viewModel.viewState<SleepViewState>()
        .collectAsState(initial = SleepViewState())
    val playerState by playerViewModel.viewState<MainScreenViewState>()
        .collectAsState(initial = MainScreenViewState())

    val isActive = state.isSleeping
    val alarmLabel = stringResource(Res.string.alarm_label)

    Box(modifier = Modifier.fillMaxSize()) {
        Image(
            painter = painterResource(Res.drawable.bg_art),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.75f))
        )

        Column(
            modifier = modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            SpacerHelper(32.dp)

            Text(
                text = stringResource(Res.string.sleep),
                style = MaterialTheme.typography.headlineLarge,
                fontFamily = balooFont,
                fontWeight = FontWeight.Normal,
                color = Mint
            )

            SpacerHelper(24.dp)

            // --- The Glowing Timer Circle ---
            GlowingTimerCircle(
                font = quickSandFont,
                isActive = isActive,
                text = formatTime(state.clockMs),
                caption = stringResource(if (isActive) Res.string.sleeping_for else Res.string.until_wake_up)
            )

            SpacerHelper(24.dp)

            Text(
                text = stringResource(if (isActive) Res.string.alarm_set_for else Res.string.when_wake_up),
                style = MaterialTheme.typography.bodySmall,
                color = Color.White,
                fontFamily = quickSandFont,
                fontWeight = FontWeight.Medium
            )
            SpacerHelper(12.dp)

            val timeStyle = MaterialTheme.typography.displaySmall.copy(
                fontFamily = quickSandFont,
                fontWeight = FontWeight.SemiBold
            )
            if (isActive) {
                Text(
                    text = formatClock(state.wakeHour, state.wakeMinute),
                    style = timeStyle,
                    color = Color.White
                )
                if (state.alarmScheduled == false) {
                    SpacerHelper(8.dp)
                    Text(
                        text = stringResource(Res.string.alarm_failed),
                        fontFamily = quickSandFont,
                        color = Color(0xFFFF8A80),
                        fontSize = 14.sp,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(horizontal = 32.dp)
                    )
                }
            } else {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    WheelPicker(
                        values = (0..23).toList(),
                        selected = state.wakeHour,
                        onSelected = { viewModel.updateWakeTime(it, state.wakeMinute) },
                        textStyle = timeStyle
                    )
                    Text(":", style = timeStyle, color = Color.White)
                    WheelPicker(
                        values = (0..59).toList(),
                        selected = state.wakeMinute,
                        onSelected = { viewModel.updateWakeTime(state.wakeHour, it) },
                        textStyle = timeStyle
                    )
                }
            }

            SpacerHelper(20.dp)

            DozzzeButton(
                onClicked = {
                    if (isActive) {
                        viewModel.wakeUp()
                        if (playerState.isPlaying) playerViewModel.togglePlayPause()
                    } else {
                        viewModel.startSleep(alarmLabel)
                        // plays the last sound; its sleep timer starts with it
                        if (!playerState.isPlaying) playerViewModel.playSound(playerState.selectedSoundKey)
                    }
                },
                buttonText = stringResource(if (isActive) Res.string.wake_up else Res.string.set_alarm_start),
                backgroundColor = if (isActive) DarkNavy else Color.White,
                textColor = if (isActive) LightGray else DarkNavy,
                buttonWidth = 220.dp,
                buttonTextStyle = MaterialTheme.typography.displaySmall,
                buttonTextFont = quickSandFont,
                buttonTextWeight = FontWeight.Normal,
                shape = RoundedCornerShape(20.dp)
            )

            if (isActive) {
                TextButton(onClick = { viewModel.cancelSleep() }) {
                    Text(
                        stringResource(Res.string.not_sleeping_yet),
                        fontFamily = quickSandFont,
                        color = Color.White.copy(alpha = 0.6f)
                    )
                }
            }

            SpacerHelper(32.dp)
        }
    }

    // --- Morning check-in ---
    state.sessionToRate?.let { night ->
        AlertDialog(
            onDismissRequest = { viewModel.skipRating() },
            title = { Text(stringResource(Res.string.how_did_you_sleep), fontFamily = quickSandFont) },
            text = {
                Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
                    Text(
                        "${stringResource(Res.string.you_slept)} ${formatDuration(night.durationMs ?: 0)}",
                        fontFamily = quickSandFont
                    )
                    SpacerHelper(12.dp)
                    RatingBar(
                        currentRating = 0,
                        onRatingChanged = { viewModel.rateNight(it) },
                        activeColor = Color(0xFFFFC107),
                        inactiveColor = Color.Gray.copy(alpha = 0.5f)
                    )
                }
            },
            confirmButton = {},
            dismissButton = {
                TextButton(onClick = { viewModel.skipRating() }) {
                    Text(stringResource(Res.string.skip), fontFamily = quickSandFont)
                }
            }
        )
    }
}
