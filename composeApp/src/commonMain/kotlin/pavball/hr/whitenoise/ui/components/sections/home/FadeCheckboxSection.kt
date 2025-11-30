package pavball.hr.whitenoise.ui.components.sections.home

import androidx.compose.foundation.layout.Row
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import pavball.hr.whitenoise.ui.viewmodels.MainScreenViewModel
import pavball.hr.whitenoise.ui.viewmodels.MainScreenViewState

@Composable
internal fun FadeCheckboxSection(
    state: MainScreenViewState,
    viewModel: MainScreenViewModel,
    time: String
) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Checkbox(
            checked = state.fadeEnabled,
            onCheckedChange = { viewModel.updateFadeEnabled(it) }
        )
        Text("Fade out last 30s")

        //Text("Fade out last {$time}s")
    }
}
