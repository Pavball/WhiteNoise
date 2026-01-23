package pavball.hr.whitenoise.ui.screens.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.intl.Locale
import androidx.compose.ui.text.toUpperCase
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import pavball.hr.whitenoise.ui.components.button.DozzzeButton
import pavball.hr.whitenoise.ui.components.utils.SpacerHelper
import pavball.hr.whitenoise.ui.screens.main.Screens
import pavball.hr.whitenoise.ui.theme.getCustomQuicksandFontFamily
import whitenoise.composeapp.generated.resources.Res
import whitenoise.composeapp.generated.resources.bg_green
import whitenoise.composeapp.generated.resources.dreams
import whitenoise.composeapp.generated.resources.logo
import whitenoise.composeapp.generated.resources.meditations
import whitenoise.composeapp.generated.resources.sleep
import whitenoise.composeapp.generated.resources.tracking
import whitenoise.composeapp.generated.resources.white_noise


@Composable
internal fun HomeScreen(
    modifier: Modifier = Modifier,
    navController: NavController
) {

    val quickSandFont = getCustomQuicksandFontFamily()

    Box(modifier = Modifier.fillMaxSize()) {
        Image(
            painter = painterResource(Res.drawable.bg_green),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.6f))
        )

        Column(
            modifier = modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Image(
                painter = painterResource(Res.drawable.logo),
                contentDescription = null,
                modifier = modifier.size(106.dp)
            )

            SpacerHelper(65.dp)

            DozzzeButton(
                onClicked = { navController.navigate(Screens.Dreams.route) },
                buttonText = stringResource(Res.string.dreams).toUpperCase(Locale.current),
                buttonWidth = 226.dp,
                buttonTextStyle = MaterialTheme.typography.bodyMedium,
                buttonTextFont = quickSandFont,
                buttonTextWeight = FontWeight.Bold,
                shape = RoundedCornerShape(16.dp)
            )

            SpacerHelper(16.dp)

            DozzzeButton(
                onClicked = { navController.navigate(Screens.Meditations.route) },
                buttonText = stringResource(Res.string.meditations).toUpperCase(Locale.current),
                buttonWidth = 226.dp,
                buttonTextStyle = MaterialTheme.typography.bodyMedium,
                buttonTextFont = quickSandFont,
                buttonTextWeight = FontWeight.Bold,
                shape = RoundedCornerShape(16.dp)
            )

            SpacerHelper(16.dp)

            DozzzeButton(
                onClicked = { navController.navigate(Screens.WhiteNoise.route) },
                buttonText = stringResource(Res.string.white_noise).toUpperCase(Locale.current),
                buttonWidth = 226.dp,
                buttonTextStyle = MaterialTheme.typography.bodyMedium,
                buttonTextFont = quickSandFont,
                buttonTextWeight = FontWeight.Bold,
                shape = RoundedCornerShape(16.dp)
            )

            SpacerHelper(16.dp)

            DozzzeButton(
                onClicked = { navController.navigate(Screens.Tracking.route) },
                buttonText = stringResource(Res.string.tracking).toUpperCase(Locale.current),
                buttonWidth = 226.dp,
                buttonTextStyle = MaterialTheme.typography.bodyMedium,
                buttonTextFont = quickSandFont,
                buttonTextWeight = FontWeight.Bold,
                shape = RoundedCornerShape(16.dp)
            )

            SpacerHelper(16.dp)

            DozzzeButton(
                onClicked = { navController.navigate(Screens.Sleep.route) },
                buttonText = stringResource(Res.string.sleep).toUpperCase(Locale.current),
                buttonWidth = 226.dp,
                buttonTextStyle = MaterialTheme.typography.bodyMedium,
                buttonTextFont = quickSandFont,
                buttonTextWeight = FontWeight.Bold,
                shape = RoundedCornerShape(16.dp)
            )
        }
    }

}