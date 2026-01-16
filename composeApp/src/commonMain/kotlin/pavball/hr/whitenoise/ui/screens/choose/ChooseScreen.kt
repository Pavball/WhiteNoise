package pavball.hr.whitenoise.ui.screens.choose

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import org.jetbrains.compose.resources.stringResource
import pavball.hr.whitenoise.ui.components.button.DozzzeButton
import pavball.hr.whitenoise.ui.components.utils.SpacerHelper
import pavball.hr.whitenoise.ui.screens.main.Screens
import whitenoise.composeapp.generated.resources.Res
import whitenoise.composeapp.generated.resources.dreams
import whitenoise.composeapp.generated.resources.meditations
import whitenoise.composeapp.generated.resources.min
import whitenoise.composeapp.generated.resources.sleep
import whitenoise.composeapp.generated.resources.tracking
import whitenoise.composeapp.generated.resources.white_noise


@Composable
internal fun HomeScreen(
    modifier: Modifier = Modifier,
    navController: NavController
) {

    Column(modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally) {

        DozzzeButton(
            onClicked = {},
            buttonText = stringResource(Res.string.dreams)
        )

        SpacerHelper(16.dp)

        DozzzeButton(
            onClicked = {},
            buttonText = stringResource(Res.string.meditations)
        )

        SpacerHelper(16.dp)

        DozzzeButton(
            onClicked = { navController.navigate(Screens.WhiteNoise.route) },
            buttonText = stringResource(Res.string.white_noise)
        )

        SpacerHelper(16.dp)

        DozzzeButton(
            onClicked = {},
            buttonText = stringResource(Res.string.tracking)
        )

        SpacerHelper(16.dp)

        DozzzeButton(
            onClicked = {},
            buttonText = stringResource(Res.string.sleep)
        )


    }

}