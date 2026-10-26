package pavball.hr.whitenoise.ui.screens.meditation

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import pavball.hr.whitenoise.domain.model.Mood
import pavball.hr.whitenoise.ui.components.button.DozzzeButton
import pavball.hr.whitenoise.ui.components.effects.GlowingTimerCircle
import pavball.hr.whitenoise.ui.components.utils.SpacerHelper
import pavball.hr.whitenoise.ui.components.utils.formatTime
import pavball.hr.whitenoise.ui.theme.DarkNavy
import pavball.hr.whitenoise.ui.theme.LightGray
import pavball.hr.whitenoise.ui.theme.Mint
import pavball.hr.whitenoise.ui.theme.getCustomBalooFontFamily
import pavball.hr.whitenoise.ui.theme.getCustomQuicksandFontFamily
import pavball.hr.whitenoise.ui.viewmodels.BreathAction
import pavball.hr.whitenoise.ui.viewmodels.MeditationPhase
import pavball.hr.whitenoise.ui.viewmodels.MeditationViewModel
import pavball.hr.whitenoise.ui.viewmodels.MeditationViewState
import whitenoise.composeapp.generated.resources.Res
import whitenoise.composeapp.generated.resources.bg_sea
import whitenoise.composeapp.generated.resources.breathe_in
import whitenoise.composeapp.generated.resources.breathe_out
import whitenoise.composeapp.generated.resources.carry_on
import whitenoise.composeapp.generated.resources.end
import whitenoise.composeapp.generated.resources.hold
import whitenoise.composeapp.generated.resources.how_do_you_feel
import whitenoise.composeapp.generated.resources.meditation_for
import whitenoise.composeapp.generated.resources.meditations
import whitenoise.composeapp.generated.resources.min
import whitenoise.composeapp.generated.resources.mood_anxiety
import whitenoise.composeapp.generated.resources.mood_happy
import whitenoise.composeapp.generated.resources.mood_sad
import whitenoise.composeapp.generated.resources.pause
import whitenoise.composeapp.generated.resources.resume
import whitenoise.composeapp.generated.resources.start_again
import whitenoise.composeapp.generated.resources.well_done

private val Mood.label: StringResource
    get() = when (this) {
        Mood.ANXIETY -> Res.string.mood_anxiety
        Mood.SAD -> Res.string.mood_sad
        Mood.HAPPY -> Res.string.mood_happy
    }

@Composable
internal fun MeditationScreen(
    modifier: Modifier = Modifier,
    viewModel: MeditationViewModel = koinViewModel()
) {

    val balooFont = getCustomBalooFontFamily()
    val quickSandFont = getCustomQuicksandFontFamily()

    val state by viewModel.viewState<MeditationViewState>()
        .collectAsState(initial = MeditationViewState())

    val isActive = state.phase == MeditationPhase.RUNNING || state.phase == MeditationPhase.PAUSED
    val glowScale by animateFloatAsState(state.breathScale, tween(durationMillis = 300), label = "breath")

    Box(modifier = Modifier.fillMaxSize()) {
        Image(
            painter = painterResource(Res.drawable.bg_sea),
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
                text = stringResource(Res.string.meditations),
                style = MaterialTheme.typography.headlineLarge,
                fontFamily = balooFont,
                fontWeight = FontWeight.Normal
            )

            SpacerHelper(40.dp)

            GlowingTimerCircle(
                font = quickSandFont,
                isActive = isActive,
                text = formatTime(state.remainingMs),
                caption = when (state.breathStep?.action) {
                    BreathAction.INHALE -> stringResource(Res.string.breathe_in)
                    BreathAction.HOLD -> stringResource(Res.string.hold)
                    BreathAction.EXHALE -> stringResource(Res.string.breathe_out)
                    null -> null
                },
                glowScale = if (state.phase == MeditationPhase.RUNNING) glowScale else 1f
            )

            SpacerHelper(40.dp)

            when (state.phase) {
                MeditationPhase.CHOOSING -> MoodAndDurationPicker(state, quickSandFont, viewModel)

                MeditationPhase.RUNNING, MeditationPhase.PAUSED -> {
                    Text(
                        text = stringResource(Res.string.meditation_for),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onPrimaryFixed,
                        fontFamily = quickSandFont,
                        fontWeight = FontWeight.Medium
                    )
                    SpacerHelper(12.dp)
                    Text(
                        text = stringResource((state.mood ?: Mood.HAPPY).label),
                        style = MaterialTheme.typography.displaySmall,
                        color = MaterialTheme.colorScheme.onPrimaryFixed,
                        fontFamily = quickSandFont,
                        fontWeight = FontWeight.SemiBold
                    )
                }

                MeditationPhase.FINISHED -> Text(
                    text = stringResource(Res.string.well_done),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onPrimaryFixed,
                    fontFamily = quickSandFont,
                    fontWeight = FontWeight.Medium,
                    textAlign = TextAlign.Center
                )
            }

            SpacerHelper(25.dp)

            DozzzeButton(
                onClicked = {
                    when (state.phase) {
                        MeditationPhase.CHOOSING -> viewModel.start()
                        MeditationPhase.RUNNING -> viewModel.pause()
                        MeditationPhase.PAUSED -> viewModel.resume()
                        MeditationPhase.FINISHED -> viewModel.reset()
                    }
                },
                buttonText = stringResource(
                    when (state.phase) {
                        MeditationPhase.CHOOSING -> Res.string.carry_on
                        MeditationPhase.RUNNING -> Res.string.pause
                        MeditationPhase.PAUSED -> Res.string.resume
                        MeditationPhase.FINISHED -> Res.string.start_again
                    }
                ),
                backgroundColor = if (isActive) DarkNavy else Color.White,
                textColor = if (isActive) LightGray else DarkNavy,
                buttonWidth = 170.dp,
                buttonTextStyle = MaterialTheme.typography.displaySmall,
                buttonTextFont = quickSandFont,
                buttonTextWeight = FontWeight.Normal,
                shape = RoundedCornerShape(20.dp)
            )

            if (isActive) {
                TextButton(onClick = { viewModel.finish() }) {
                    Text(
                        stringResource(Res.string.end),
                        fontFamily = quickSandFont,
                        color = Color.White.copy(alpha = 0.6f)
                    )
                }
            }

            SpacerHelper(32.dp)
        }
    }

}

@Composable
private fun MoodAndDurationPicker(
    state: MeditationViewState,
    font: FontFamily,
    viewModel: MeditationViewModel
) {
    Text(
        text = stringResource(Res.string.how_do_you_feel),
        style = MaterialTheme.typography.bodySmall,
        color = MaterialTheme.colorScheme.onPrimaryFixed,
        fontFamily = font,
        fontWeight = FontWeight.Medium
    )
    SpacerHelper(12.dp)

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center
    ) {
        Mood.entries.forEach { mood ->
            val selected = state.mood == mood
            TextButton(onClick = { viewModel.selectMood(mood) }) {
                Text(
                    text = stringResource(mood.label),
                    style = MaterialTheme.typography.displaySmall,
                    color = if (selected) Mint else MaterialTheme.colorScheme.onPrimaryFixed.copy(alpha = 0.7f),
                    fontFamily = font,
                    fontWeight = if (selected) FontWeight.Bold else FontWeight.SemiBold
                )
            }
        }
    }

    Row(horizontalArrangement = Arrangement.Center) {
        state.durationOptions.forEach { minutes ->
            val selected = state.durationMinutes == minutes
            TextButton(onClick = { viewModel.selectDuration(minutes) }) {
                Text(
                    text = "$minutes ${stringResource(Res.string.min)}",
                    fontFamily = font,
                    fontSize = if (selected) 18.sp else 16.sp,
                    fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium,
                    color = if (selected) Color.White else Color.White.copy(alpha = 0.5f)
                )
            }
        }
    }
}
