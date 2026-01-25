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
import pavball.hr.whitenoise.ui.screens.home.HomeScreen
import pavball.hr.whitenoise.ui.screens.dream.DreamScreen
import pavball.hr.whitenoise.ui.screens.meditation.MeditationScreen
import pavball.hr.whitenoise.ui.screens.notes.NotesScreen
import pavball.hr.whitenoise.ui.screens.profile.ManageProfileScreen
import pavball.hr.whitenoise.ui.screens.profile.NotificationsScreen
import pavball.hr.whitenoise.ui.screens.profile.ProfileScreen
import pavball.hr.whitenoise.ui.screens.profile.SecurityScreen
import pavball.hr.whitenoise.ui.screens.settings.SettingsScreen
import pavball.hr.whitenoise.ui.screens.sleep.SleepScreen
import pavball.hr.whitenoise.ui.screens.sound.add.AddSoundScreen
import pavball.hr.whitenoise.ui.screens.sound.manage.ManageCustomSoundsScreen
import pavball.hr.whitenoise.ui.screens.tracking.TrackingScreen
import pavball.hr.whitenoise.ui.screens.whitenoise.WhiteNoiseScreen
import pavball.hr.whitenoise.ui.screens.whitenoise.WhiteNoiseScreen2
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
                SettingsScreen(navController = navController)
            }

            // WHITE NOISE PART

            composable(Screens.WhiteNoise.route) {
                WhiteNoiseScreen2(navController = navController, viewModel = viewModel)
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

            composable(Screens.AddSound.route) {
                AddSoundScreen(viewModel = viewModel)
            }

            // WHITE NOISE PART END

            composable(Screens.Dreams.route) {
                DreamScreen()
            }

            composable(Screens.Meditations.route) {
                MeditationScreen()
            }

            composable(Screens.Tracking.route) {
                TrackingScreen()
            }

            composable(Screens.Sleep.route) {
                SleepScreen()
            }

            composable(Screens.Notes.route) {
                NotesScreen()
            }

            composable(Screens.Profile.route) {
                ProfileScreen(navController = navController)
            }

            composable(Screens.ManageProfile.route) {
                ManageProfileScreen()
            }

            composable(Screens.Security.route) {
                SecurityScreen()
            }

            composable(Screens.Notifications.route) {
                NotificationsScreen()
            }


        }
    }
}
