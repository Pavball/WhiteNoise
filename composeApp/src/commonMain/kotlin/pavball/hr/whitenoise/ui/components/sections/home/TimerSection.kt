package pavball.hr.whitenoise.ui.components.sections.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import pavball.hr.whitenoise.ui.components.SpacerHelper
import pavball.hr.whitenoise.ui.viewmodels.MainScreenViewModel
import pavball.hr.whitenoise.ui.viewmodels.MainScreenViewState

@Composable
internal fun TimerSection(
    state: MainScreenViewState,
    viewModel: MainScreenViewModel
) {

    Text("Sleep Timer", style = MaterialTheme.typography.titleMedium)
    SpacerHelper(8.dp)

    LazyVerticalGrid(
        columns = GridCells.Fixed(3),
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(max = 260.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        contentPadding = PaddingValues(horizontal = 8.dp)
    ) {
        items(state.timerOptions) { minutes ->
            Button(
                onClick = { viewModel.updateSelectedTimer(minutes) },
                colors = ButtonDefaults.buttonColors(
                    containerColor =
                        if (state.timerSelectedMinutes == minutes)
                            Color.Blue.copy(alpha = 0.7f)
                        else Color.Gray.copy(alpha = 0.4f),
                    contentColor = Color.White
                )
            ) {
                Text("$minutes min")
            }
        }
    }

    SpacerHelper(16.dp)

}
