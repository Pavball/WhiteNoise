package pavball.hr.whitenoise.ui.components

import androidx.compose.foundation.layout.size
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import org.jetbrains.compose.resources.painterResource
import pavball.hr.whitenoise.ui.screens.main.Screens
import whitenoise.composeapp.generated.resources.Res
import whitenoise.composeapp.generated.resources.ic_pause

@Composable
fun BottomNavigationBar(modifier: Modifier = Modifier, navController: NavController) {
    val items = listOf(Screens.Home,Screens.ManageCustomSounds, Screens.Options)

    BottomAppBar(
        contentColor = Color.White,
        modifier = Modifier.shadow(elevation = 16.dp),
    ) {
        val currentRoute = navController.currentBackStackEntryAsState().value?.destination?.route
        NavigationBar {
            items.forEach { tab ->
                NavigationBarItem(
                    icon = {
                        Icon(
                            painter = tab.icon(),
                            contentDescription = null,
                            modifier = modifier.size(24.dp)
                        )
                    },
                    label = { Text(tab.title) },
                    selected = currentRoute == tab.route,
                    onClick = {
                        if (currentRoute != tab.route) {
                            navController.navigate(tab.route) {
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                    }
                )
            }
        }
    }
}