package pavball.hr.whitenoise.ui.screens.options

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import pavball.hr.whitenoise.ui.components.drawFadingEdges
import pavball.hr.whitenoise.ui.viewmodels.MainScreenViewModel
import pavball.hr.whitenoise.ui.viewmodels.MainScreenViewState
import whitenoise.composeapp.generated.resources.Res
import whitenoise.composeapp.generated.resources.fade_out_time
import whitenoise.composeapp.generated.resources.fade_time
import whitenoise.composeapp.generated.resources.test
import whitenoise.composeapp.generated.resources.theme

@Composable
fun OptionsScreen(
    modifier: Modifier = Modifier,
    navController: NavController
) {

    val viewModel = koinViewModel<MainScreenViewModel>()
    val state by viewModel.viewState<MainScreenViewState>()
        .collectAsState(initial = MainScreenViewState())
    var themeExpanded by rememberSaveable { mutableStateOf(false) }
    var fadeOutExpanded by rememberSaveable { mutableStateOf(false) }

    val scrollableState = rememberLazyListState()

    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(12.dp),
        horizontalAlignment = Alignment.Start,
        modifier = Modifier
            .fillMaxSize()
            .drawFadingEdges(scrollableState),
        state = scrollableState
    ) {

        item {

            Text(
                stringResource(Res.string.theme),
                fontSize = 18.sp,
                modifier = Modifier.padding(start = 24.dp, 24.dp, end = 24.dp)
            )

            Button(
                modifier = Modifier.padding(24.dp),
                onClick = { themeExpanded = !themeExpanded }) {
                Text(
                    "${stringResource(Res.string.theme)}: ${state.themeMode}",
                    color = Color.White
                )

                DropdownMenu(
                    expanded = themeExpanded,
                    onDismissRequest = { themeExpanded = false }) {
                    state.themeOptions.forEach { mode ->
                        DropdownMenuItem(
                            text = { Text(mode) },
                            onClick = {
                                themeExpanded = false
                                viewModel.saveThemeModeToUserPrefs(mode)
                            }
                        )
                    }
                }
            }

            HorizontalDivider(thickness = 2.dp, color = MaterialTheme.colorScheme.primaryContainer)
        }

        item {
            Text(
                stringResource(Res.string.fade_out_time),
                fontSize = 18.sp,
                modifier = Modifier.padding(start = 24.dp, 24.dp, end = 24.dp)
            )

            Button(
                modifier = Modifier.padding(24.dp),
                onClick = { fadeOutExpanded = !fadeOutExpanded }) {
                Text(
                    "${stringResource(Res.string.fade_time)}: ${state.fadeDuration}",
                    color = Color.White
                )

                DropdownMenu(
                    expanded = fadeOutExpanded,
                    onDismissRequest = { fadeOutExpanded = false }) {
                    state.fadeOptions.forEach { mode ->
                        DropdownMenuItem(
                            text = { Text(text = mode.toString()) },
                            onClick = {
                                fadeOutExpanded = false
                                viewModel.saveFadeOutTimeToUserPrefs(mode)
                            }
                        )
                    }
                }
            }

            HorizontalDivider(thickness = 2.dp, color = MaterialTheme.colorScheme.primaryContainer)
        }

        item {
            Text(stringResource(Res.string.test), modifier = Modifier.padding(24.dp))

            HorizontalDivider(thickness = 2.dp, color = MaterialTheme.colorScheme.primaryContainer)
        }

        item {
            Text(stringResource(Res.string.test), modifier = Modifier.padding(24.dp))

            HorizontalDivider(thickness = 2.dp, color = MaterialTheme.colorScheme.primaryContainer)
        }
    }
}