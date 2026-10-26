package pavball.hr.whitenoise.ui.screens.main

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import pavball.hr.whitenoise.domain.model.JournalType
import pavball.hr.whitenoise.ui.components.appbar.BottomNavigationBar
import pavball.hr.whitenoise.ui.screens.dream.DreamScreen
import pavball.hr.whitenoise.ui.screens.home.HomeScreen
import pavball.hr.whitenoise.ui.screens.meditation.MeditationScreen
import pavball.hr.whitenoise.ui.screens.notes.NotesScreen
import pavball.hr.whitenoise.ui.screens.settings.SettingsScreen
import pavball.hr.whitenoise.ui.screens.sleep.SleepScreen
import pavball.hr.whitenoise.ui.screens.sound.add.AddSoundScreen
import pavball.hr.whitenoise.ui.screens.sound.select.SelectSoundScreen
import pavball.hr.whitenoise.ui.screens.tracking.TrackingScreen
import pavball.hr.whitenoise.ui.screens.whitenoise.WhiteNoiseScreen
import pavball.hr.whitenoise.ui.viewmodels.MainScreenViewModel

/**
 * [viewModel] is the activity-scoped instance from [App]; it is passed down
 * instead of resolved per destination so every screen shares the same
 * playback connection and state.
 */
@Composable
internal fun MainScreen(viewModel: MainScreenViewModel) {
    val navController = rememberNavController()
    Scaffold(
        topBar = { },
        bottomBar = { BottomNavigationBar(navController = navController) }
    ) { paddingValues ->

        NavHost(
            navController = navController,
            startDestination = Screens.Home.route,
            modifier = Modifier.padding(paddingValues)
        ) {

            composable(Screens.Home.route) {
                HomeScreen(navController = navController, playerViewModel = viewModel)
            }

            composable(Screens.Options.route) {
                SettingsScreen(
                    viewModel = viewModel,
                    onOpenMySounds = { navController.navigate(Screens.AddSound.route) }
                )
            }

            // WHITE NOISE PART

            composable(Screens.WhiteNoise.route) {
                WhiteNoiseScreen(navController = navController, viewModel = viewModel)
            }

            composable(Screens.AddSound.route) {
                AddSoundScreen(viewModel = viewModel)
            }

            composable(Screens.SelectSound.route) {
                SelectSoundScreen(navController = navController, viewModel = viewModel)
            }

            // WHITE NOISE PART END

            composable(
                route = Screens.JournalEditor.route,
                arguments = listOf(
                    navArgument(Screens.JournalEditor.ARG_TYPE) { type = NavType.StringType },
                    navArgument(Screens.JournalEditor.ARG_ID) {
                        type = NavType.LongType
                        defaultValue = Screens.JournalEditor.NO_ID
                    }
                )
            ) { entry ->
                val args = entry.savedStateHandle
                val type = JournalType.entries.firstOrNull {
                    it.name == args.get<String>(Screens.JournalEditor.ARG_TYPE)
                } ?: JournalType.DREAM
                val id = args.get<Long>(Screens.JournalEditor.ARG_ID)
                    ?.takeIf { it != Screens.JournalEditor.NO_ID }

                DreamScreen(
                    type = type,
                    entryId = id,
                    onSaved = {
                        // A dream written from Home lands in Notes; otherwise go back.
                        if (navController.previousBackStackEntry?.destination?.route == Screens.Home.route) {
                            navController.navigate(Screens.Notes.route) {
                                popUpTo(Screens.JournalEditor.route) { inclusive = true }
                            }
                        } else {
                            navController.popBackStack()
                        }
                    }
                )
            }

            composable(Screens.Meditations.route) {
                MeditationScreen()
            }

            composable(Screens.Tracking.route) {
                TrackingScreen(
                    onWriteNote = { navController.navigate(Screens.JournalEditor.route(JournalType.NOTE)) }
                )
            }

            composable(Screens.Sleep.route) {
                SleepScreen(playerViewModel = viewModel)
            }

            composable(Screens.Notes.route) {
                NotesScreen(
                    onNewEntry = { type -> navController.navigate(Screens.JournalEditor.route(type)) },
                    onEditEntry = { entry ->
                        navController.navigate(Screens.JournalEditor.route(entry.type, entry.id))
                    }
                )
            }
        }
    }
}
