package pavball.hr.whitenoise.ui.screens.main

import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import pavball.hr.whitenoise.ui.components.BottomNavigationBar
import pavball.hr.whitenoise.ui.components.TopToolbar
import pavball.hr.whitenoise.ui.screens.options.OptionsScreen
import pavball.hr.whitenoise.ui.screens.home.HomeScreen

@Composable
fun MainScreen() {
    val navController = rememberNavController()
    Scaffold(
        topBar = { TopToolbar(navController) },
        bottomBar = { BottomNavigationBar(navController = navController) }
    ) {

            NavHost(
                navController = navController,
                startDestination = Screens.Home.route,
            ) {
                composable(Screens.Home.route) {
                    HomeScreen(navController = navController)
                }

                composable(Screens.Options.route) {
                    OptionsScreen(
                        navController = navController
                    )
                }

            }
        }
}




