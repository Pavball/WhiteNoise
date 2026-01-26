package pavball.hr.whitenoise.ui.screens.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import org.koin.compose.koinInject
import org.koin.compose.viewmodel.koinViewModel
import pavball.hr.whitenoise.ui.components.auth.AuthInput
import pavball.hr.whitenoise.ui.components.auth.AuthLabel
import pavball.hr.whitenoise.ui.screens.main.Screens
import pavball.hr.whitenoise.ui.theme.DarkNavy
import pavball.hr.whitenoise.ui.theme.LightGray
import pavball.hr.whitenoise.ui.theme.SlateBlue
import pavball.hr.whitenoise.ui.theme.getCustomQuicksandFontFamily
import pavball.hr.whitenoise.ui.viewmodels.AuthState
import pavball.hr.whitenoise.ui.viewmodels.ProfileViewModel
import pavball.hr.whitenoise.ui.viewmodels.ProfileViewState


@Composable
fun LoginScreen(
    navController: NavController
) {
    val quickSand = getCustomQuicksandFontFamily()

    val viewModel = koinViewModel<ProfileViewModel>()
    val state by viewModel.viewState<ProfileViewState>()

        .collectAsState(initial = ProfileViewState())
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    LaunchedEffect(state.authState) {
        if (state.authState is AuthState.Success) {
            viewModel.resetAuthState()
            navController.popBackStack()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(LightGray)
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.Start
        ) {
            Text(
                text = "Form Log In",
                fontFamily = quickSand,
                color = Color.LightGray,
                fontSize = 14.sp,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            AuthLabel("Email", quickSand)
            AuthInput(value = email, onValueChange = { email = it })

            Spacer(Modifier.height(16.dp))

            AuthLabel("Password", quickSand)
            AuthInput(value = password, onValueChange = { password = it }, isPassword = true)

            Spacer(Modifier.height(24.dp))

            if (state.authState is AuthState.Error) {
                Text(
                    text = (state.authState as AuthState.Error).message,
                    color = Color.Red,
                    fontFamily = quickSand,
                    fontSize = 14.sp,
                    modifier = Modifier.padding(bottom = 12.dp)
                )
            }

            Button(
                onClick = { viewModel.login(email, password) },
                colors = ButtonDefaults.buttonColors(containerColor = DarkNavy),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.fillMaxWidth().height(50.dp)
            ) {
                if (state.authState is AuthState.Loading) {
                    CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                } else {
                    Text("Sign In", fontFamily = quickSand, color = Color.White)
                }
            }

            Spacer(Modifier.height(16.dp))

            Text(
                text = "Forgot password?",
                fontFamily = quickSand,
                color = SlateBlue,
                textDecoration = TextDecoration.Underline,
                modifier = Modifier.clickable { navController.navigate(Screens.ForgotPassword.route) }
            )
        }
    }
}

// --- Helper Composables to match the Input Design ---

