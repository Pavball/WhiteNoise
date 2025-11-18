package pavball.hr.whitenoise.ui.screens.main

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.painter.Painter
import org.jetbrains.compose.resources.painterResource
import whitenoise.composeapp.generated.resources.Res
import whitenoise.composeapp.generated.resources.ic_edit
import whitenoise.composeapp.generated.resources.ic_home
import whitenoise.composeapp.generated.resources.ic_settings

sealed class Screens(val route: String, val title: String, val icon: @Composable () -> Painter) {
    data object Home :
        Screens(
            route = "home",
            title = "Home",
            icon = { painterResource(Res.drawable.ic_home) }
        )

    data object ManageCustomSounds :
        Screens(
            route = "manage_sounds",
            title = "Custom Sounds",
            icon = { painterResource(Res.drawable.ic_edit) }
        )

    data object Options :
        Screens(
            route = "options",
            title = "Options",
            icon = { painterResource(Res.drawable.ic_settings) }
        )
}
