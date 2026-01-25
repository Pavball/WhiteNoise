package pavball.hr.whitenoise.ui.screens.profile

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import org.jetbrains.compose.resources.painterResource
import pavball.hr.whitenoise.ui.components.button.DozzzeButton
import pavball.hr.whitenoise.ui.components.utils.SpacerHelper
import pavball.hr.whitenoise.ui.screens.main.Screens
import pavball.hr.whitenoise.ui.theme.Mint
import pavball.hr.whitenoise.ui.theme.getCustomBalooFontFamily
import pavball.hr.whitenoise.ui.theme.getCustomQuicksandFontFamily
import whitenoise.composeapp.generated.resources.Res
import whitenoise.composeapp.generated.resources.bg_mountains

@Composable
fun ProfileScreen(modifier: Modifier = Modifier, navController: NavController) {

    val balooFont = getCustomBalooFontFamily()
    val quickSandFont = getCustomQuicksandFontFamily()

    var isActive by remember { mutableStateOf(false) }

    Box(modifier = Modifier.fillMaxSize()) {
        Image(
            painter = painterResource(Res.drawable.bg_mountains),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.75f))
        )

        Column(
            modifier = modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Text(
                text = "Profile",
                style = MaterialTheme.typography.headlineLarge,
                fontFamily = balooFont,
                fontWeight = FontWeight.Normal,
                color = Mint
            )


            SpacerHelper(90.dp)

            DozzzeButton(
                modifier = modifier.height(70.dp).padding(bottom = 16.dp),
                onClicked = { navController.navigate(Screens.ManageProfile.route) },
                buttonText = "Manage profile",
                buttonWidth = 250.dp,
                buttonTextStyle = MaterialTheme.typography.bodyMedium,
                buttonTextFont = quickSandFont,
                buttonTextWeight = FontWeight.Bold,
                shape = RoundedCornerShape(20.dp)
            )

            DozzzeButton(
                modifier = modifier.height(90.dp).padding(bottom = 16.dp),
                onClicked = {  navController.navigate(Screens.Security.route)},
                buttonText = "Password & security",
                buttonWidth = 250.dp,
                buttonTextStyle = MaterialTheme.typography.bodyMedium,
                buttonTextFont = quickSandFont,
                buttonTextWeight = FontWeight.Bold,
                shape = RoundedCornerShape(20.dp)
            )

            DozzzeButton(
                modifier = modifier.height(70.dp).padding(bottom = 16.dp),
                onClicked = {  navController.navigate(Screens.Notifications.route)},
                buttonText = "Notifications",
                buttonWidth = 250.dp,
                buttonTextStyle = MaterialTheme.typography.bodyMedium,
                buttonTextFont = quickSandFont,
                buttonTextWeight = FontWeight.Bold,
                shape = RoundedCornerShape(20.dp)
            )

            DozzzeButton(
                modifier = modifier.height(70.dp).padding(bottom = 16.dp),
                onClicked = { },
                buttonText = "Sound",
                buttonWidth = 250.dp,
                buttonTextStyle = MaterialTheme.typography.bodyMedium,
                buttonTextFont = quickSandFont,
                buttonTextWeight = FontWeight.Bold,
                shape = RoundedCornerShape(20.dp)
            )


        }

    }

}
