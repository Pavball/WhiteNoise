package pavball.hr.whitenoise.ui.screens.main

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.painter.Painter
import org.jetbrains.compose.resources.painterResource
import pavball.hr.whitenoise.domain.model.JournalType
import whitenoise.composeapp.generated.resources.Res
import whitenoise.composeapp.generated.resources.ic_add
import whitenoise.composeapp.generated.resources.ic_edit
import whitenoise.composeapp.generated.resources.ic_home
import whitenoise.composeapp.generated.resources.ic_notes
import whitenoise.composeapp.generated.resources.ic_settings

sealed class Screens(val route: String, val title: String, val icon: @Composable () -> Painter) {

    data object Home :
        Screens(
            route = "home",
            title = "Home",
            icon = { painterResource(Res.drawable.ic_home) }
        )
    data object WhiteNoise :
        Screens(
            route = "white_noise",
            title = "White Noise",
            icon = { painterResource(Res.drawable.ic_home) }
        )

    data object SelectSound :
        Screens(
            route = "select_sounds",
            title = "Select Sounds",
            icon = { painterResource(Res.drawable.ic_edit) }
        )

    data object AddSound :
        Screens(
            route = "add_sound",
            title = "Add Sound",
            icon = { painterResource(Res.drawable.ic_add) }
        )

    data object Options :
        Screens(
            route = "options",
            title = "Options",
            icon = { painterResource(Res.drawable.ic_settings) }
        )

    /** Writes a new dream / sleep note, or edits one when an id is given. */
    data object JournalEditor :
        Screens(
            route = "journal/{type}?id={id}",
            title = "Journal",
            icon = { painterResource(Res.drawable.ic_edit) }
        ) {
        const val ARG_TYPE = "type"
        const val ARG_ID = "id"
        const val NO_ID = -1L

        fun route(type: JournalType, id: Long? = null): String =
            "journal/${type.name}" + (id?.let { "?id=$it" } ?: "")
    }

    data object Meditations :
        Screens(
            route = "meditations",
            title = "Meditations",
            icon = { painterResource(Res.drawable.ic_settings) }
        )

    data object Tracking :
        Screens(
            route = "tracking",
            title = "Tracking",
            icon = { painterResource(Res.drawable.ic_settings) }
        )

    data object Sleep :
        Screens(
            route = "sleep",
            title = "Sleep",
            icon = { painterResource(Res.drawable.ic_settings) }
        )

    data object Notes :
        Screens(
            route = "notes",
            title = "Notes",
            icon = { painterResource(Res.drawable.ic_notes) }
        )

}
