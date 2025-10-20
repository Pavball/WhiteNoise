package pavball.hr.whitenoise.ui.screens.main

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Home
import androidx.compose.ui.graphics.vector.ImageVector


sealed class Screens(val route: String, val title: String, val icon: ImageVector) {
    data object Home : Screens("home", "Home", Icons.Default.Home)
    data object Options : Screens("options", "Options", Icons.Default.Build)
}
