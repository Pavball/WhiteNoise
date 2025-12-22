package pavball.hr.whitenoise.ui.components.sections.home

import androidx.compose.foundation.layout.Row
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import org.jetbrains.compose.resources.stringResource
import pavball.hr.whitenoise.ui.viewmodels.MainScreenViewModel
import pavball.hr.whitenoise.ui.viewmodels.MainScreenViewState
import whitenoise.composeapp.generated.resources.Res
import whitenoise.composeapp.generated.resources.fade_out_last
import whitenoise.composeapp.generated.resources.sec
import whitenoise.composeapp.generated.resources.select_sound

@Composable
internal fun FadeCheckboxSection(
    state: MainScreenViewState,
    viewModel: MainScreenViewModel
) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Checkbox(
            checked = state.fadeEnabled,
            colors = CheckboxDefaults.colors().copy(checkedBoxColor = Color.Blue.copy(alpha = 0.7f), checkedCheckmarkColor = Color.White),
            onCheckedChange = { viewModel.updateFadeEnabled(it) }
        )
        Text("${stringResource(Res.string.fade_out_last)} ${state.fadeDuration} ${stringResource(Res.string.sec)}")

    }
}
