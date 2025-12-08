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
import androidx.navigation.NavController
import org.koin.compose.viewmodel.koinViewModel
import pavball.hr.whitenoise.ui.components.drawFadingEdges
import pavball.hr.whitenoise.ui.viewmodels.MainScreenViewModel
import pavball.hr.whitenoise.ui.viewmodels.MainScreenViewState

@Composable
fun OptionsScreen(
    modifier: Modifier = Modifier,
    navController: NavController
) {

    val viewModel = koinViewModel<MainScreenViewModel>()
    val state by viewModel.viewState<MainScreenViewState>()
        .collectAsState(initial = MainScreenViewState())
    var themeExpanded by rememberSaveable { mutableStateOf(false) }

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
            Button(
                modifier = Modifier.padding(24.dp),
                onClick = { themeExpanded = !themeExpanded }) {
                Text("Theme: ${state.themeMode.replaceFirstChar { it.uppercase() }}")

                DropdownMenu(
                    expanded = themeExpanded,
                    onDismissRequest = { themeExpanded = false }) {
                    listOf("system", "light", "dark").forEach { mode ->
                        DropdownMenuItem(
                            text = { Text(mode.replaceFirstChar { it.uppercase() }) },
                            onClick = {
                                themeExpanded = false
                                viewModel.saveThemeModeToUserPrefs(mode)
                            }
                        )
                    }
                }
            }

            HorizontalDivider(thickness = 2.dp, color = Color.Black.copy(alpha = 0.4f))
        }

        item {
            Text("This is text string for item in options", modifier = Modifier.padding(24.dp))

            HorizontalDivider(thickness = 2.dp, color = Color.Black.copy(alpha = 0.4f))
        }

        item {
            Text("This is text string for item in options 2", modifier = Modifier.padding(24.dp))

            HorizontalDivider(thickness = 2.dp, color = Color.Black.copy(alpha = 0.4f))
        }

        item {
            Text("This is text string for item in options 3", modifier = Modifier.padding(24.dp))

            HorizontalDivider(thickness = 2.dp, color = Color.Black.copy(alpha = 0.4f))
        }
    }
}