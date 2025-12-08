package pavball.hr.whitenoise.ui.components.sections.home

import androidx.compose.foundation.layout.Row
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import pavball.hr.whitenoise.ui.viewmodels.MainScreenViewModel
import pavball.hr.whitenoise.ui.viewmodels.MainScreenViewState

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
        Text("Fade out last ${state.fadeDuration} sec")

    }
}
