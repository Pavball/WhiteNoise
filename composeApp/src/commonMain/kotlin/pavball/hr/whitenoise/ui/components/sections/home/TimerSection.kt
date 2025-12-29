package pavball.hr.whitenoise.ui.components.sections.home

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.unit.dp
import org.jetbrains.compose.resources.stringResource
import pavball.hr.whitenoise.ui.components.HorizontalSnapPicker
import pavball.hr.whitenoise.ui.components.SpacerHelper
import pavball.hr.whitenoise.ui.viewmodels.MainScreenViewModel
import pavball.hr.whitenoise.ui.viewmodels.MainScreenViewState
import whitenoise.composeapp.generated.resources.Res
import whitenoise.composeapp.generated.resources.sleep_timer

@Composable
internal fun TimerSection(
    state: MainScreenViewState,
    onMinutesSelected: (Int) -> Unit
) {

    val initialIndex = remember(state.timerOptions, state.timerSelectedMinutes) {
        val index = state.timerOptions.indexOf(state.timerSelectedMinutes)
        if (index >= 0) index else 0
    }

    Text(stringResource(Res.string.sleep_timer), style = MaterialTheme.typography.titleMedium)
    SpacerHelper(8.dp)

    HorizontalSnapPicker(
        items = state.timerOptions,
        startIndex = initialIndex,
        onSelect = { selectedMinute ->
            onMinutesSelected(selectedMinute)
        }
    )

    SpacerHelper(8.dp)
    Text(stringResource(Res.string.sleep_timer), style = MaterialTheme.typography.bodyMedium)

    SpacerHelper(16.dp)

    /*
        LazyRow(
            modifier = Modifier
                .fillMaxWidth()
                .drawHorizontalFadingEdges(scrollableState),
            state = scrollableState,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            contentPadding = PaddingValues(horizontal = 8.dp)
        ) {
            items(state.timerOptions) { minutes ->
                Button(
                    onClick = { viewModel.updateSelectedTimer(minutes) },
                    colors = ButtonDefaults.buttonColors(
                        containerColor =
                            if (state.timerSelectedMinutes != minutes)
                                Color.Gray.copy(alpha = 0.4f) else Color.Unspecified,
                        contentColor = Color.White
                    )
                ) {
                    Text("$minutes ${stringResource(Res.string.min)}")
                }
            }
        }
    */

    /*
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
                        if (state.timerSelectedMinutes != minutes)
                             Color.Gray.copy(alpha = 0.4f) else Color.Unspecified,
                    contentColor = Color.White
                )
            ) {
                Text("$minutes ${stringResource(Res.string.min)}")
            }
        }
    }*/


}
