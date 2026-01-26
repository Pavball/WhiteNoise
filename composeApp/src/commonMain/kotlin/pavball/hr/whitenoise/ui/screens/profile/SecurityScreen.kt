package pavball.hr.whitenoise.ui.screens.profile

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import org.jetbrains.compose.resources.painterResource
import pavball.hr.whitenoise.ui.components.utils.SpacerHelper
import pavball.hr.whitenoise.ui.screens.auth.ForgotPasswordScreen
import pavball.hr.whitenoise.ui.screens.main.Screens
import pavball.hr.whitenoise.ui.theme.DarkNavy
import pavball.hr.whitenoise.ui.theme.LightGray
import pavball.hr.whitenoise.ui.theme.Mint
import pavball.hr.whitenoise.ui.theme.getCustomBalooFontFamily
import pavball.hr.whitenoise.ui.theme.getCustomQuicksandFontFamily
import whitenoise.composeapp.generated.resources.Res
import whitenoise.composeapp.generated.resources.bg_cloudynight
import whitenoise.composeapp.generated.resources.ic_arrowright
import whitenoise.composeapp.generated.resources.ic_info
import whitenoise.composeapp.generated.resources.ic_key
import whitenoise.composeapp.generated.resources.ic_phone
import whitenoise.composeapp.generated.resources.ic_profile

@Composable
fun SecurityScreen(
    modifier: Modifier = Modifier,
    navController: NavController
) {
    val balooFont = getCustomBalooFontFamily()
    val quickSandFont = getCustomQuicksandFontFamily()

    // Local State for the Switch
    var phoneIdEnabled by remember { mutableStateOf(true) }

    Box(modifier = Modifier.fillMaxSize()) {
        // 1. Background
        Image(
            painter = painterResource(Res.drawable.bg_cloudynight),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )

        // 2. Dark Overlay
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.3f))
        )

        // 3. Content
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            SpacerHelper(60.dp)

            Text(
                text = "Password & security",
                style = MaterialTheme.typography.headlineLarge,
                fontFamily = balooFont,
                fontWeight = FontWeight.Normal,
                color = Mint
            )

            SpacerHelper(30.dp)

            // --- Row 1: Change Password (Arrow) ---
            SecurityOptionRow(
                icon = painterResource(Res.drawable.ic_profile), // Placeholder for Person/Lock icon
                text = "Change Password",
                fontFamily = quickSandFont,
                modifier = Modifier.clickable { /* Navigate to Change Password */ },
                trailingContent = {
                    ArrowIcon()
                }
            )

            Spacer(modifier = Modifier.height(16.dp))

            // --- Row 2: Phone ID (Switch) ---
            SecurityOptionRow(
                icon = painterResource(Res.drawable.ic_phone),
                text = "Phone ID",
                fontFamily = quickSandFont,
                trailingContent = {
                    Switch(
                        checked = phoneIdEnabled,
                        onCheckedChange = { phoneIdEnabled = it },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = LightGray,
                            checkedTrackColor = Mint,
                            checkedBorderColor = Color.Transparent,
                            uncheckedThumbColor = LightGray,
                            uncheckedTrackColor = DarkNavy,
                            uncheckedBorderColor = Color.Transparent
                        ),
                        modifier = Modifier.scale(0.9f)
                    )
                }
            )

            Spacer(modifier = Modifier.height(16.dp))

            // --- Row 3: Forgot Password (Arrow) ---
            SecurityOptionRow(
                icon = painterResource(Res.drawable.ic_info), // Your key icon
                text = "Forgot Password",
                fontFamily = quickSandFont,
                modifier = Modifier.clickable {
                    navController.navigate(Screens.ForgotPassword.route)
                },
                trailingContent = {
                    ArrowIcon()
                }
            )
        }
    }
}

/**
 * Reusable Row Component matching ProfileInputRow / NotificationRow style.
 * Accepts a 'trailingContent' composable to support either an Arrow or a Switch.
 */
@Composable
fun SecurityOptionRow(
    icon: Painter,
    text: String,
    fontFamily: FontFamily,
    modifier: Modifier = Modifier,
    trailingContent: @Composable () -> Unit
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(72.dp)
            .border(
                border = BorderStroke(1.dp, Mint),
                shape = RoundedCornerShape(16.dp)
            )
            .padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        // Left Side: Icon + Text
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.weight(1f)
        ) {
            Icon(
                painter = icon,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(24.dp)
            )

            Spacer(modifier = Modifier.width(16.dp))

            Text(
                text = text,
                color = LightGray,
                fontFamily = fontFamily,
                fontWeight = FontWeight.Normal,
                style = MaterialTheme.typography.bodyMedium
            )
        }

        // Right Side: Switch or Arrow
        trailingContent()
    }
}

@Composable
fun ArrowIcon() {
    Icon(
        painter = painterResource(Res.drawable.ic_arrowright),
        contentDescription = null, // purely decorative
        tint = Color.White,
        modifier = Modifier
            .size(24.dp)
            .padding(2.dp) // padding inside the circle
    )
}