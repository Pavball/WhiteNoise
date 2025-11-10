package pavball.hr.whitenoise.ui.screens.empty

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import org.koin.compose.viewmodel.koinViewModel
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

    Column(
        modifier = modifier
            .fillMaxSize(),
        horizontalAlignment = Alignment.Start,
        verticalArrangement = Arrangement.Center
    ) {

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            Button(onClick = { themeExpanded = !themeExpanded }) {
                Text("Theme: ${state.themeMode.replaceFirstChar { it.uppercase() }}")

                DropdownMenu(expanded = themeExpanded, onDismissRequest = { themeExpanded = false }) {
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


        }

    }
}