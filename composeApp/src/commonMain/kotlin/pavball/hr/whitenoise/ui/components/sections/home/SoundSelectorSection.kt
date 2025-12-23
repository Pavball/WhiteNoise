package pavball.hr.whitenoise.ui.components.sections.home

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import org.jetbrains.compose.resources.stringResource
import pavball.hr.whitenoise.ui.components.SpacerHelper
import pavball.hr.whitenoise.ui.viewmodels.MainScreenViewModel
import pavball.hr.whitenoise.ui.viewmodels.MainScreenViewState
import whitenoise.composeapp.generated.resources.Res
import whitenoise.composeapp.generated.resources.add_custom_sound

@Composable
internal fun SoundSelectorSection(
    expanded: Boolean,
    onExpandedChange: (Boolean) -> Unit,
    combinedSounds: List<Pair<String, String>>,
    selectedSoundLabel: String,
    launchSoundPicker: () -> Unit,
    viewModel: MainScreenViewModel,
    state: MainScreenViewState
) {

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Button(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp),
            onClick = { onExpandedChange(!expanded) },
            shape = RoundedCornerShape(12.dp),
            border = BorderStroke(1.dp, Color.Black.copy(alpha = 0.75f))
        ) {
            Text(selectedSoundLabel, color = Color.White)

            DropdownMenu(expanded, onDismissRequest = { onExpandedChange(false) }) {
                combinedSounds.forEach { (label, id) ->
                    DropdownMenuItem(
                        text = { Text(label, fontWeight = FontWeight.Bold) },
                        onClick = {
                            onExpandedChange(false)
                            viewModel.updateSelectedSoundKey(id)
                            viewModel.playSound(id)
                            viewModel.startTimer(
                                if (state.timerSelectedMinutes != 0)
                                    state.timerSelectedMinutes else 1,
                                state.fadeEnabled
                            )
                        }
                    )
                }
            }
        }

        SpacerHelper(12.dp)

        Button(
            onClick = { launchSoundPicker() }
        ) { Text(stringResource(Res.string.add_custom_sound), color = Color.White) }

        SpacerHelper(28.dp)
    }
}