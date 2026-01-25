package pavball.hr.whitenoise.ui.screens.sleep

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
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
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import pavball.hr.whitenoise.ui.components.button.DozzzeButton
import pavball.hr.whitenoise.ui.components.effects.GlowingTimerCircle
import pavball.hr.whitenoise.ui.components.utils.SpacerHelper
import pavball.hr.whitenoise.ui.theme.DarkNavy
import pavball.hr.whitenoise.ui.theme.LightGray
import pavball.hr.whitenoise.ui.theme.getCustomBalooFontFamily
import pavball.hr.whitenoise.ui.theme.getCustomQuicksandFontFamily
import whitenoise.composeapp.generated.resources.Res
import whitenoise.composeapp.generated.resources.bg_art
import whitenoise.composeapp.generated.resources.sleep

@Composable
fun SleepScreen(modifier: Modifier = Modifier) {

    val balooFont = getCustomBalooFontFamily()
    val quickSandFont = getCustomQuicksandFontFamily()

    var isActive by remember { mutableStateOf(false) }

    Box(modifier = Modifier.fillMaxSize()) {
        Image(
            painter = painterResource(Res.drawable.bg_art),
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
                text = stringResource(Res.string.sleep),
                style = MaterialTheme.typography.headlineLarge,
                fontFamily = balooFont,
                fontWeight = FontWeight.Normal,
                color = Color(0xFFA8E6CF) // Mint color from previous screen
            )

            // Adjusted spacing to fit the large circle
            SpacerHelper(40.dp)

            // --- The Glowing Timer Circle ---
            GlowingTimerCircle(
                font = quickSandFont,
                isActive = isActive
            )

            SpacerHelper(40.dp)

            Text(
                text = if (isActive) "Alarm set for" else "When do you wanna wake up?",
                style = MaterialTheme.typography.bodySmall,
                color = Color.White, // Enforce white as per screenshot
                fontFamily = quickSandFont,
                fontWeight = FontWeight.Medium
            )
            SpacerHelper(20.dp)

            Column(
                modifier = modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                if (!isActive) {
                    Text(
                        modifier = modifier.padding(bottom = 4.dp),
                        text = "4    59",
                        style = MaterialTheme.typography.displaySmall,
                        color = Color.White.copy(alpha = 0.4f),
                        fontFamily = quickSandFont,
                        fontWeight = FontWeight.SemiBold
                    )
                }

                Text(
                    modifier = modifier.padding(bottom = 4.dp),
                    text = "5    00",
                    style = MaterialTheme.typography.displaySmall,
                    color = Color.White,
                    fontFamily = quickSandFont,
                    fontWeight = FontWeight.SemiBold
                )
                if (!isActive) {
                    Text(
                        modifier = modifier.padding(end = 4.dp),
                        text = "5    01",
                        style = MaterialTheme.typography.displaySmall,
                        color = Color.White.copy(alpha = 0.4f),
                        fontFamily = quickSandFont,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }

            SpacerHelper(10.dp)

            DozzzeButton(
                onClicked = { isActive = !isActive },
                buttonText = if (isActive) "Wake up?" else "Set alarm & start sleep timer",
                backgroundColor = if (isActive) DarkNavy else Color.White,
                textColor = if (isActive) LightGray else DarkNavy,
                buttonWidth = 200.dp,
                buttonTextStyle = MaterialTheme.typography.displaySmall,
                buttonTextFont = quickSandFont,
                buttonTextWeight = FontWeight.Normal,
                shape = RoundedCornerShape(20.dp)
            )
        }
    }
}
