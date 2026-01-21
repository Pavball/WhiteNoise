package pavball.hr.whitenoise.ui.screens.main

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import org.koin.compose.viewmodel.koinViewModel
import pavball.hr.whitenoise.ui.components.appbar.BottomNavigationBar
import pavball.hr.whitenoise.ui.components.appbar.TopToolbar
import pavball.hr.whitenoise.ui.screens.choose.HomeScreen
import pavball.hr.whitenoise.ui.screens.options.OptionsScreen
import pavball.hr.whitenoise.ui.screens.home.WhiteNoiseScreen
import pavball.hr.whitenoise.ui.screens.manage.ManageCustomSoundsScreen
import pavball.hr.whitenoise.ui.viewmodels.MainScreenViewModel
import pavball.hr.whitenoise.ui.viewmodels.MainScreenViewState

@Composable
fun MainScreen() {
    val navController = rememberNavController()
    Scaffold(
        topBar = { },
        bottomBar = { BottomNavigationBar(navController = navController) }
    ) { paddingValues ->
        val viewModel = koinViewModel<MainScreenViewModel>()
        val state by viewModel.viewState<MainScreenViewState>().collectAsState(initial = MainScreenViewState())

        NavHost(
            navController = navController,
            startDestination = Screens.Home.route,
            modifier = Modifier.padding(paddingValues)
        ) {

            composable(Screens.Home.route) {
                HomeScreen(navController = navController)
            }

            composable(Screens.Options.route) {
                OptionsScreen(navController = navController)
            }

            // WHITE NOISE PART

            composable(Screens.WhiteNoise.route) {
                WhiteNoiseScreen(viewModel = viewModel)
            }

            composable(Screens.ManageCustomSounds.route) {
                ManageCustomSoundsScreen(
                    customSounds = state.customSounds,
                    onRename = { sound, newName -> viewModel.renameCustomSound(sound.id, newName) },
                    onDelete = { sound -> viewModel.removeUserSound(sound.id) },
                    onColorChange = { cs, colorId ->
                        viewModel.updateCustomSoundColor(id = cs.id, colorId = colorId)
                    },
                )
            }

            // WHITE NOISE PART END



            composable(Screens.Dreams.route) {
                OptionsScreen(navController = navController)
            }

            composable(Screens.Meditations.route) {
                OptionsScreen(navController = navController)
            }

            composable(Screens.Tracking.route) {
                OptionsScreen(navController = navController)
            }

            composable(Screens.Sleep.route) {
                OptionsScreen(navController = navController)
            }

        }
    }
}
