package pavball.hr.whitenoise.ui.screens.profile

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import org.jetbrains.compose.resources.painterResource
import org.koin.compose.koinInject
import org.koin.compose.viewmodel.koinViewModel
import pavball.hr.whitenoise.ui.components.button.DozzzeButton
import pavball.hr.whitenoise.ui.components.utils.SpacerHelper
import pavball.hr.whitenoise.ui.screens.main.Screens
import pavball.hr.whitenoise.ui.theme.Mint
import pavball.hr.whitenoise.ui.theme.getCustomBalooFontFamily
import pavball.hr.whitenoise.ui.theme.getCustomQuicksandFontFamily
import pavball.hr.whitenoise.ui.viewmodels.MainScreenViewModel
import pavball.hr.whitenoise.ui.viewmodels.ProfileViewModel
import pavball.hr.whitenoise.ui.viewmodels.ProfileViewState
import whitenoise.composeapp.generated.resources.Res
import whitenoise.composeapp.generated.resources.bg_mountains

@Composable
fun ProfileScreen(
    modifier: Modifier = Modifier,
    navController: NavController
) {
    val balooFont = getCustomBalooFontFamily()
    val quickSandFont = getCustomQuicksandFontFamily()

    val viewModel = koinViewModel<ProfileViewModel>()

    val state by viewModel.viewState<ProfileViewState>()
        .collectAsState(initial = ProfileViewState())

    val currentUser = state.currentUser

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
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            SpacerHelper(60.dp)

            Text(
                text = "Profile",
                style = MaterialTheme.typography.headlineLarge,
                fontFamily = balooFont,
                fontWeight = FontWeight.Normal,
                color = Mint
            )

            SpacerHelper(30.dp)

            // --- Profile Picture Circle ---
            Box(
                modifier = Modifier
                    .height(180.dp)
                    .clip(CircleShape)
                    .background(Color(0xFFE8EAF6)),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = if (currentUser != null) "Welcome\n${currentUser.username}" else "Set\nprofile\npicture",
                        fontFamily = quickSandFont,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1B2236),
                        textAlign = TextAlign.Center,
                        fontSize = 18.sp,
                        modifier = Modifier.padding(30.dp)
                    )
                }
            }

            SpacerHelper(30.dp)

            if (currentUser != null) {
                // === LOGGED IN ===
                DozzzeButton(
                    modifier = modifier.height(70.dp).padding(bottom = 16.dp),
                    onClicked = { navController.navigate(Screens.ManageProfile.route) },
                    buttonText = "Manage profile",
                    buttonWidth = 250.dp,
                    buttonTextStyle = MaterialTheme.typography.bodyMedium,
                    buttonTextFont = quickSandFont,
                    buttonTextWeight = FontWeight.Bold,
                    shape = RoundedCornerShape(16.dp)
                )

                DozzzeButton(
                    modifier = modifier.height(90.dp).padding(bottom = 16.dp),
                    onClicked = { navController.navigate(Screens.Security.route) },
                    buttonText = "Password & security",
                    buttonWidth = 250.dp,
                    buttonTextStyle = MaterialTheme.typography.bodyMedium,
                    buttonTextFont = quickSandFont,
                    buttonTextWeight = FontWeight.Bold,
                    shape = RoundedCornerShape(16.dp)
                )

                DozzzeButton(
                    modifier = modifier.height(70.dp).padding(bottom = 16.dp),
                    onClicked = { navController.navigate(Screens.Notifications.route) },
                    buttonText = "Notifications",
                    buttonWidth = 250.dp,
                    buttonTextStyle = MaterialTheme.typography.bodyMedium,
                    buttonTextFont = quickSandFont,
                    buttonTextWeight = FontWeight.Bold,
                    shape = RoundedCornerShape(16.dp)
                )

            } else {
                // === LOGGED OUT ===
                DozzzeButton(
                    modifier = modifier.height(70.dp).padding(bottom = 16.dp),
                    onClicked = { navController.navigate("register") },
                    buttonText = "Register",
                    buttonWidth = 250.dp,
                    buttonTextStyle = MaterialTheme.typography.bodyMedium,
                    buttonTextFont = quickSandFont,
                    buttonTextWeight = FontWeight.Bold,
                    shape = RoundedCornerShape(16.dp)
                )

                DozzzeButton(
                    modifier = modifier.height(70.dp).padding(bottom = 16.dp),
                    onClicked = { navController.navigate(Screens.Notifications.route) },
                    buttonText = "Notifications",
                    buttonWidth = 250.dp,
                    buttonTextStyle = MaterialTheme.typography.bodyMedium,
                    buttonTextFont = quickSandFont,
                    buttonTextWeight = FontWeight.Bold,
                    shape = RoundedCornerShape(16.dp)
                )

                DozzzeButton(
                    modifier = modifier.height(70.dp).padding(bottom = 16.dp),
                    onClicked = { /* Navigate Sound */ },
                    buttonText = "Sound",
                    buttonWidth = 250.dp,
                    buttonTextStyle = MaterialTheme.typography.bodyMedium,
                    buttonTextFont = quickSandFont,
                    buttonTextWeight = FontWeight.Bold,
                    shape = RoundedCornerShape(16.dp)
                )

                TextButton(onClick = { navController.navigate("login") }) {
                    Text(
                        "Already have an account? Log In",
                        color = Color.White,
                        fontFamily = quickSandFont
                    )
                }
            }
        }
    }
}