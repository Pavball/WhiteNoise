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
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import org.jetbrains.compose.resources.painterResource
import org.koin.compose.koinInject
import org.koin.compose.viewmodel.koinViewModel
import pavball.hr.whitenoise.ui.components.utils.SpacerHelper
import pavball.hr.whitenoise.ui.theme.LightGray
import pavball.hr.whitenoise.ui.theme.Mint
import pavball.hr.whitenoise.ui.theme.getCustomBalooFontFamily
import pavball.hr.whitenoise.ui.theme.getCustomQuicksandFontFamily
import pavball.hr.whitenoise.ui.viewmodels.ProfileViewModel
import pavball.hr.whitenoise.ui.viewmodels.ProfileViewState
import whitenoise.composeapp.generated.resources.Res
import whitenoise.composeapp.generated.resources.bg_cloudynight
import whitenoise.composeapp.generated.resources.ic_arrowright
import whitenoise.composeapp.generated.resources.ic_inbox
import whitenoise.composeapp.generated.resources.ic_info
import whitenoise.composeapp.generated.resources.ic_log_out
import whitenoise.composeapp.generated.resources.ic_phone
import whitenoise.composeapp.generated.resources.ic_profile

@Composable
fun ManageProfileScreen(
    modifier: Modifier = Modifier,
    navController: NavController,
) {
    val balooFont = getCustomBalooFontFamily()
    val quickSandFont = getCustomQuicksandFontFamily()

    val viewModel = koinViewModel<ProfileViewModel>()
    val state by viewModel.viewState<ProfileViewState>()
        .collectAsState(initial = ProfileViewState())

    val currentUser = state.currentUser

    // Local state for inputs
    var username by remember { mutableStateOf(currentUser?.username ?: "") }
    var email by remember { mutableStateOf(currentUser?.email ?: "") }
    var phoneNumber by remember { mutableStateOf("") }

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
                text = "Manage Profile",
                style = MaterialTheme.typography.headlineLarge,
                fontFamily = balooFont,
                fontWeight = FontWeight.Normal,
                color = Mint
            )

            SpacerHelper(70.dp)

            // --- Input Fields ---

            ProfileInputRow(
                icon = painterResource(Res.drawable.ic_profile),
                value = username,
                onValueChange = { username = it },
                placeholder = "Full Name",
                fontFamily = quickSandFont
            )

            SpacerHelper(16.dp)

            ProfileInputRow(
                icon = painterResource(Res.drawable.ic_phone),
                value = phoneNumber,
                onValueChange = { phoneNumber = it },
                placeholder = "Phone",
                fontFamily = quickSandFont
            )

            SpacerHelper(16.dp)

            ProfileInputRow(
                icon = painterResource(Res.drawable.ic_inbox),
                value = email,
                onValueChange = { email = it },
                placeholder = "Email",
                fontFamily = quickSandFont
            )

            SpacerHelper(16.dp)

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(72.dp)
                    .border(
                        border = BorderStroke(1.dp, Mint),
                        shape = RoundedCornerShape(16.dp)
                    )
                    .clip(RoundedCornerShape(16.dp))
                    .clickable { viewModel.logout(); navController.popBackStack() }
                    .padding(horizontal = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    painter = painterResource(Res.drawable.ic_log_out),
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(24.dp)
                )

                Spacer(modifier = Modifier.width(16.dp))

                Text(
                    text = "Log Out",
                    color = Color.White,
                    fontFamily = quickSandFont,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium
                )

                // Push Arrow to the right
                Spacer(modifier = Modifier.weight(1f))

                ArrowIcon()
            }

            SpacerHelper(40.dp)
        }
    }
}

/**
 * Reusable Input Row styled exactly like NotificationRow
 */
@Composable
fun ProfileInputRow(
    icon: Painter,
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    fontFamily: androidx.compose.ui.text.font.FontFamily
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(72.dp)
            .border(
                border = BorderStroke(1.dp, Mint),
                shape = RoundedCornerShape(16.dp)
            )
            .padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // 1. Icon (Left)
        Icon(
            painter = icon,
            contentDescription = null,
            tint = Color.White,
            modifier = Modifier.size(24.dp)
        )

        Spacer(modifier = Modifier.width(16.dp))

        // 2. Input Field (Middle - Takes available space)
        Box(
            modifier = Modifier.weight(1f),
            contentAlignment = Alignment.CenterStart
        ) {
            if (value.isEmpty()) {
                Text(
                    text = placeholder,
                    color = LightGray,
                    fontFamily = fontFamily,
                    fontSize = 24.sp
                )
            }
            BasicTextField(
                value = value,
                onValueChange = onValueChange,
                textStyle = TextStyle(
                    color = LightGray,
                    fontFamily = fontFamily,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Normal
                ),
                cursorBrush = SolidColor(Mint),
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
        }

        // 3. Arrow (Right)
        Spacer(modifier = Modifier.width(8.dp))
        ArrowIcon()
    }
}
