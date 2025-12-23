package pavball.hr.whitenoise.ui.components.sections.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import org.jetbrains.compose.resources.painterResource
import pavball.hr.whitenoise.ui.components.SpacerHelper
import pavball.hr.whitenoise.ui.viewmodels.MainScreenViewModel
import pavball.hr.whitenoise.ui.viewmodels.MainScreenViewState
import whitenoise.composeapp.generated.resources.Res
import whitenoise.composeapp.generated.resources.ic_pause
import whitenoise.composeapp.generated.resources.ic_play
import whitenoise.composeapp.generated.resources.ic_stop

@Composable
internal fun ControlsSection(
    state: MainScreenViewState,
    progress: Float,
    viewModel: MainScreenViewModel
) {

    Row(
        horizontalArrangement = Arrangement.spacedBy(24.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(onClick = {
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
        }) {
            Icon(
                painterResource(if (state.isPlaying) Res.drawable.ic_pause else Res.drawable.ic_play),
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primaryContainer
            )
        }

        IconButton(onClick = {
            viewModel.stopSound()
            viewModel.cancelTimer()
        }) {
            Icon(
                painterResource(Res.drawable.ic_stop),
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primaryContainer
            )
        }
    }

    SpacerHelper(24.dp)

    LinearProgressIndicator(
        progress = { progress },
        modifier = Modifier
            .fillMaxWidth(0.8f)
            .height(8.dp)
            .clip(RoundedCornerShape(12.dp)),
        trackColor = Color.LightGray.copy(alpha = 0.3f)
    )

    SpacerHelper(28.dp)
}
