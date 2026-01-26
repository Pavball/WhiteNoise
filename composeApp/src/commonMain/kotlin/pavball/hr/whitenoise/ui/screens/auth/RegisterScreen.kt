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
import pavball.hr.whitenoise.ui.theme.DarkNavy
import pavball.hr.whitenoise.ui.theme.LightGray
import pavball.hr.whitenoise.ui.theme.SlateBlue
import pavball.hr.whitenoise.ui.theme.getCustomQuicksandFontFamily
import pavball.hr.whitenoise.ui.viewmodels.AuthState
import pavball.hr.whitenoise.ui.viewmodels.ProfileViewModel
import pavball.hr.whitenoise.ui.viewmodels.ProfileViewState

@Composable
fun RegisterScreen(
    navController: NavController,
) {
    val quickSand = getCustomQuicksandFontFamily()

    // State
    val viewModel = koinViewModel<ProfileViewModel>()
    val state by viewModel.viewState<ProfileViewState>().collectAsState(initial = ProfileViewState())
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var repeatPassword by remember { mutableStateOf("") }

    // Success Listener
    LaunchedEffect(state.authState) {
        if (state.authState is AuthState.Success) {
            viewModel.resetAuthState()
            navController.popBackStack()
        }
    }

    // Full Screen Container (acts as the modal background)
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(LightGray) // Gray background like image
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.Start
        ) {
            // Header
            Text(
                text = "Form Register",
                fontFamily = quickSand,
                color = Color.LightGray,
                fontSize = 14.sp,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            // --- Form Card Area ---

            // Email
            AuthLabel("Email", quickSand)
            AuthInput(value = email, onValueChange = { email = it })

            Spacer(Modifier.height(16.dp))

            // Password
            AuthLabel("Password", quickSand)
            AuthInput(value = password, onValueChange = { password = it }, isPassword = true)

            Spacer(Modifier.height(16.dp))

            // Repeat Password
            AuthLabel("Repeat password", quickSand)
            AuthInput(value = repeatPassword, onValueChange = { repeatPassword = it }, isPassword = true)

            Spacer(Modifier.height(24.dp))

            // Error Message
            if (state.authState is AuthState.Error) {
                Text(
                    text = (state.authState as AuthState.Error).message,
                    color = Color.Red,
                    fontFamily = quickSand,
                    fontSize = 14.sp,
                    modifier = Modifier.padding(bottom = 12.dp)
                )
            }

            // Register Button
            Button(
                onClick = {
                    if (password == repeatPassword) viewModel.register("User", email, password)
                    else { /* Show local error about mismatch */ }
                },
                colors = ButtonDefaults.buttonColors(containerColor = DarkNavy),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.fillMaxWidth().height(50.dp)
            ) {
                if (state.authState is AuthState.Loading) {
                    CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                } else {
                    Text("Register", fontFamily = quickSand, color = Color.White)
                }
            }

            Spacer(Modifier.height(16.dp))

            Text(
                text = "Already have an account?",
                fontFamily = quickSand,
                color = SlateBlue,
                textDecoration = TextDecoration.Underline,
                modifier = Modifier.clickable {
                    navController.navigate("login")
                }
            )
        }
    }
}