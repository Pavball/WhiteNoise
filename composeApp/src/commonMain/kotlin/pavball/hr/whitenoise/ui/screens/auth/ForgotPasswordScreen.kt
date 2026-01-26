package pavball.hr.whitenoise.ui.screens.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import pavball.hr.whitenoise.ui.components.auth.AuthInput
import pavball.hr.whitenoise.ui.components.auth.AuthLabel
import pavball.hr.whitenoise.ui.theme.DarkNavy
import pavball.hr.whitenoise.ui.theme.LightGray
import pavball.hr.whitenoise.ui.theme.getCustomQuicksandFontFamily

@Composable
fun ForgotPasswordScreen(
    navController: NavController
) {
    val quickSand = getCustomQuicksandFontFamily()
    var email by remember { mutableStateOf("") }

    // Full Screen Container (Modal Background)
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(LightGray) // Matches your modal background color
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.Start
        ) {

            Text(
                text = "Form Forgot Password",
                fontFamily = quickSand,
                color = Color.LightGray,
                fontSize = 14.sp,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            // Email Input
            AuthLabel("Email", quickSand)
            AuthInput(value = email, onValueChange = { email = it })

            Spacer(Modifier.height(24.dp))

            // Buttons Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceEvenly // Pushes buttons to the right
            ) {
                // Cancel Button (Text)
                Text(
                    text = "Cancel",
                    fontFamily = quickSand,
                    color = DarkNavy,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Normal,
                    modifier = Modifier
                        .clickable { navController.popBackStack() }
                        .padding(horizontal = 16.dp)
                )

                // Reset Password Button (Solid)
                Button(
                    onClick = {
                        // Add logic here to send reset email
                        navController.popBackStack()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = DarkNavy),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.height(50.dp)
                ) {
                    Text(
                        "Reset Password",
                        fontFamily = quickSand,
                        color = Color.White,
                        fontWeight = FontWeight.Normal
                    )
                }
            }
        }
    }
}