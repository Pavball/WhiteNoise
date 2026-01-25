package pavball.hr.whitenoise.ui.components.appbar

import androidx.compose.foundation.layout.size
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import pavball.hr.whitenoise.ui.screens.main.Screens
import pavball.hr.whitenoise.ui.theme.DarkNavy
import pavball.hr.whitenoise.ui.theme.Mint

@Composable
fun BottomNavigationBar(modifier: Modifier = Modifier, navController: NavController) {
    val items = listOf(Screens.Home, Screens.Notes, Screens.Profile)

    BottomAppBar(
        containerColor = DarkNavy,
        contentColor = Color.White,
        modifier = Modifier.shadow(elevation = 16.dp),
    ) {
        val currentRoute = navController.currentBackStackEntryAsState().value?.destination?.route
        NavigationBar(
            containerColor = DarkNavy,
        ) {
            items.forEach { tab ->
                NavigationBarItem(
                    icon = {
                        Icon(
                            painter = tab.icon(),
                            contentDescription = null,
                            modifier = modifier.size(48.dp)
                        )
                    },
                    label = {
                    },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = Mint,
                        unselectedIconColor = MaterialTheme.colorScheme.secondary,
                        indicatorColor = Color.Transparent
                    ),
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