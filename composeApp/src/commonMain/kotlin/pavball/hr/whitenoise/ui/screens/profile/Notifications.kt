package pavball.hr.whitenoise.ui.screens.profile

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.jetbrains.compose.resources.painterResource
import pavball.hr.whitenoise.ui.components.utils.SpacerHelper
import pavball.hr.whitenoise.ui.theme.DarkNavy
import pavball.hr.whitenoise.ui.theme.LightGray
import pavball.hr.whitenoise.ui.theme.Mint
import pavball.hr.whitenoise.ui.theme.getCustomBalooFontFamily
import pavball.hr.whitenoise.ui.theme.getCustomQuicksandFontFamily
import whitenoise.composeapp.generated.resources.Res
import whitenoise.composeapp.generated.resources.bg_downclouds
import whitenoise.composeapp.generated.resources.ic_inbox
import whitenoise.composeapp.generated.resources.ic_info
import whitenoise.composeapp.generated.resources.ic_notes
import whitenoise.composeapp.generated.resources.ic_volume

@Composable
fun NotificationsScreen(modifier: Modifier = Modifier) {

    val balooFont = getCustomBalooFontFamily()
    val quickSandFont = getCustomQuicksandFontFamily()

    // State for the switches
    var appNotifications by remember { mutableStateOf(true) }
    var silentNotifications by remember { mutableStateOf(true) }
    var hideIconLabels by remember { mutableStateOf(true) }

    Box(modifier = Modifier.fillMaxSize()) {
        // Background
        Image(
            painter = painterResource(Res.drawable.bg_downclouds),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )

        // Optional: Dark overlay to ensure text readability if background is bright
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.3f))
        )

        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            SpacerHelper(60.dp) // Top padding

            Text(
                text = "Notifications",
                style = MaterialTheme.typography.headlineLarge,
                fontFamily = balooFont,
                fontWeight = FontWeight.Normal,
                color = Mint
            )

            SpacerHelper(50.dp)

            // --- Option Rows ---

            NotificationRow(
                icon = painterResource(Res.drawable.ic_info),
                text = "App Notifications",
                isChecked = appNotifications,
                onCheckedChange = { appNotifications = it },
                fontFamily = quickSandFont,
                style = MaterialTheme.typography.bodyMedium
            )

            Spacer(modifier = Modifier.height(16.dp))

            NotificationRow(
                icon = painterResource(Res.drawable.ic_volume),
                text = "Silent Notifications",
                isChecked = silentNotifications,
                onCheckedChange = { silentNotifications = it },
                fontFamily = quickSandFont,
                style = MaterialTheme.typography.bodyMedium
            )

            Spacer(modifier = Modifier.height(16.dp))

            NotificationRow(
                icon = painterResource(Res.drawable.ic_inbox), // Using Inbox as approximation for the "tray" icon
                text = "Dont show\nicon labels",
                isChecked = hideIconLabels,
                onCheckedChange = { hideIconLabels = it },
                fontFamily = quickSandFont,
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}

@Composable
fun NotificationRow(
    icon: Painter,
    text: String,
    isChecked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    style: TextStyle,
    fontFamily: FontFamily
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(72.dp) // Fixed height for consistency
            .border(
                border = BorderStroke(1.dp, Mint), // The Mint Green Outline
                shape = RoundedCornerShape(16.dp)
            )
            .padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        // Left side: Icon + Text
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.weight(1f) // Takes up available space pushing switch to right
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
                color = Color.White,
                fontFamily = fontFamily,
                style = style,
                fontWeight = FontWeight.Normal,
                lineHeight = 20.sp // Handles the two-line text nicely
            )
        }

        // Right side: Custom Switch
        Switch(
            checked = isChecked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                // ON State
                checkedThumbColor = LightGray,
                checkedTrackColor = Mint, // Light Green when ON
                checkedBorderColor = Color.Transparent,

                // OFF State
                uncheckedThumbColor = LightGray,
                uncheckedTrackColor = DarkNavy, // Dark Navy when OFF
                uncheckedBorderColor = Color.Transparent // Removes default gray border
            ),
            modifier = Modifier.scale(0.9f) // Slight scale down to match design refinement
        )
    }
}

// Helper for scaling logic if needed (Standard compose scale modifier)
