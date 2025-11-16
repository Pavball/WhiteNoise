package pavball.hr.whitenoise.ui.screens.main

import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import org.koin.compose.viewmodel.koinViewModel
import pavball.hr.whitenoise.ui.components.BottomNavigationBar
import pavball.hr.whitenoise.ui.components.TopToolbar
import pavball.hr.whitenoise.ui.screens.options.OptionsScreen
import pavball.hr.whitenoise.ui.screens.home.HomeScreen
import pavball.hr.whitenoise.ui.screens.manage.ManageCustomSoundsScreen
import pavball.hr.whitenoise.ui.viewmodels.MainScreenViewModel
import pavball.hr.whitenoise.ui.viewmodels.MainScreenViewState

@Composable
fun MainScreen() {
    val navController = rememberNavController()
    val viewModel = koinViewModel<MainScreenViewModel>()

    Scaffold(
        topBar = { TopToolbar(navController) },
        bottomBar = { BottomNavigationBar(navController = navController) }
    ) { padding ->

        val state by viewModel.viewState<MainScreenViewState>()
            .collectAsState(initial = MainScreenViewState())

        NavHost(
            navController = navController,
            startDestination = Screens.Home.route,
        ) {
            composable(Screens.Home.route) {
                HomeScreen(
                    navController = navController,
                    viewModel = viewModel
                )
            }

            composable(Screens.ManageCustomSounds.route) {
                ManageCustomSoundsScreen(
                    navController = navController,
                    customSounds = viewModel.userSounds.collectAsState().value,
                    onRename = { sound, newName ->
                        viewModel.renameCustomSound(sound.id, newName)
                    },
                    onDelete = { sound ->
                        viewModel.removeUserSound(sound.id)
                    }
                )
            }

            composable(Screens.Options.route) {
                OptionsScreen(navController = navController)
            }
        }
    }
}
